package ru.practicum.explore.privateAPI.events.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.EndpointHitDto;
import ru.practicum.explore.StatsClient;
import ru.practicum.explore.ViewStats;
import ru.practicum.explore.admin.categories.CatRepository;
import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.admin.users.UserRepository;
import ru.practicum.explore.admin.users.model.User;
import ru.practicum.explore.exception.CategoryNotFoundException;
import ru.practicum.explore.exception.EventNotFoundException;
import ru.practicum.explore.exception.RequestNotFoundException;
import ru.practicum.explore.exception.UserNotFoundException;
import ru.practicum.explore.privateAPI.events.EventRepository;
import ru.practicum.explore.privateAPI.events.LocationRepository;
import ru.practicum.explore.privateAPI.events.dto.*;
import ru.practicum.explore.privateAPI.events.model.Event;
import ru.practicum.explore.privateAPI.events.model.Location;
import ru.practicum.explore.privateAPI.events.model.State;
import ru.practicum.explore.privateAPI.mapper.EventMapper;
import ru.practicum.explore.privateAPI.requests.RequestRepository;
import ru.practicum.explore.privateAPI.requests.dto.ParticipationRequestDto;
import ru.practicum.explore.privateAPI.requests.mapper.RequestMapper;
import ru.practicum.explore.privateAPI.requests.model.Request;
import ru.practicum.explore.privateAPI.requests.model.RequestStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CatRepository catRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final EventMapper mapper;
    private final RequestMapper requestMapper;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public FullEventDto createEvent(Long userId, NewEventDto dto) {
        User user = checkUser(userId);
        Category category = checkCategory(dto.getCategory());
        Location savedLocation = locationRepository.save(dto.getLocation());
        Event event = mapper.toEvent(dto, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), State.PENDING,
                user, category, savedLocation);
        Event savedEvent = eventRepository.save(event);
        return mapper.toFullEventDto(savedEvent);
    }

    @Override
    @Transactional
    public FullEventDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = checkEvent(eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new IllegalArgumentException("Published events cannot be changed");
        }
        Event updatedEvent = EventUtil.test(event, dto);
        if (dto.getCategory() != null && !event.getCategory().getId().equals(dto.getCategory())) {
            Category category = checkCategory(dto.getCategory());
            updatedEvent.setCategory(category);
        }
        if (dto.getLocation() != null) {
            Location location = locationRepository.save(dto.getLocation());
            updatedEvent.setLocation(location);
        }
        Event savedEvent = eventRepository.save(updatedEvent);
        return mapper.toFullEventDto(savedEvent);
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequest(Long userId, Long eventId, EventRequestStatusUpdateDto dto) {
        FullEventDto event = getEventById(userId, eventId);
        List<Request> requests = requestRepository.findAllByIdIn(dto.getRequestIds());
        long availableToParticipate = event.getParticipantLimit() - event.getConfirmedRequests(); //кол мест для участия
        if (availableToParticipate == 0) {
            throw new IllegalArgumentException(String.format("Participant limit is reached for event %d", eventId));
        }
        if (!requests.isEmpty()) {
            List<Request> pendingRequests = getPendingRequests(requests);
            List<Request> confirmedRequests = new ArrayList<>();
            List<Request> rejectedRequests = new ArrayList<>();
            List<ParticipationRequestDto> savedConfirmedRequests = new ArrayList<>();
            List<ParticipationRequestDto> savedRejectedRequests = new ArrayList<>();
            if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) { //если для события лимит заявок равен 0 или отключена пре-модерация заявок
                confirmedRequests.addAll(confirmRequests(pendingRequests));
                savedConfirmedRequests.addAll(getSavedRequests(confirmedRequests));
            }
            switch (dto.getStatus()) {
                case REJECTED:
                    rejectedRequests.addAll(rejectRequests(pendingRequests));
                    savedRejectedRequests.addAll(getSavedRequests(rejectedRequests));
                    break;
                case CONFIRMED: //если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
                    if (pendingRequests.size() <= availableToParticipate) {
                        confirmedRequests.addAll(confirmRequests(pendingRequests));
                        savedConfirmedRequests.addAll(getSavedRequests(confirmedRequests));
                    } else {
                        confirmedRequests.addAll(confirmRequests(pendingRequests.stream()
                                .limit(availableToParticipate)
                                .collect(Collectors.toList())));
                        savedConfirmedRequests.addAll(getSavedRequests(confirmedRequests));
                        pendingRequests.removeAll(confirmedRequests);
                        rejectedRequests.addAll(rejectRequests(rejectRequests(pendingRequests)));
                        savedRejectedRequests.addAll(getSavedRequests(rejectedRequests));
                    }
                }
            return new EventRequestStatusUpdateResult(savedConfirmedRequests, savedRejectedRequests);
        }
        throw new RequestNotFoundException(String.format("Requests not found for event %d", eventId));
    }

    @Override
    public List<ShortEventDto> getEvents(Long userId, int from, int size) {

        return null;
    }

    @Override
    public FullEventDto getEventById(Long userId, Long eventId) {
        FullEventDto fullEventDto = mapper.toFullEventDto(checkEvent(eventId));
        List<Request> confirmedRequests = requestRepository.findAllByEventIdAndStatusOrderById(eventId, RequestStatus.CONFIRMED);
        fullEventDto.setConfirmedRequests(confirmedRequests.size());

        LocalDateTime startDateTime = LocalDateTime.now().minusYears(5).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ResponseEntity<List<ViewStats>> response = statsClient.getHits(startDateTime,
                endDateTime, true, List.of( "/events/" + eventId));
        if (response.getBody() != null) {
            fullEventDto.setViews(response.getBody().size());
        }
        return fullEventDto;
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        return requestRepository.findAllByEventId(eventId);
    }

    private List<Request> getPendingRequests(List<Request> requests) {
        List<Request> pendingRequests = requests.stream()
                .filter(request -> request.getStatus().equals(RequestStatus.PENDING))
                .collect(Collectors.toList());
        if (pendingRequests.isEmpty()) {
            throw new IllegalArgumentException("Only PENDING requests can be updated");
        }
        return pendingRequests;
    }

    private List<Request> confirmRequests(List<Request> pendingRequests) {
        return pendingRequests.stream()
                .peek(request -> request.setStatus(RequestStatus.CONFIRMED))
                .collect(Collectors.toList());
    }

    private List<Request> rejectRequests(List<Request> pendingRequests) {
        return pendingRequests.stream()
                .peek(request -> request.setStatus(RequestStatus.REJECTED))
                .collect(Collectors.toList());
    }

    private List<ParticipationRequestDto> getSavedRequests(List<Request> updatedRequests) {
        return requestRepository.saveAll(updatedRequests).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("User not found %d", userId)));
    }

    private Category checkCategory(Long catId) {
        return catRepository.findById(catId).orElseThrow(() ->
                new CategoryNotFoundException(String.format("Category not found %d", catId)));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(String.format("Event not found %d", eventId)));
    }
}
