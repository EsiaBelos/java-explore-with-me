package ru.practicum.explore.privateAPI.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.admin.users.UserRepository;
import ru.practicum.explore.admin.users.model.User;
import ru.practicum.explore.exception.EventNotFoundException;
import ru.practicum.explore.exception.RequestNotFoundException;
import ru.practicum.explore.exception.UserNotFoundException;
import ru.practicum.explore.privateAPI.events.model.Event;
import ru.practicum.explore.privateAPI.events.model.State;
import ru.practicum.explore.privateAPI.events.repository.EventRepository;
import ru.practicum.explore.privateAPI.events.service.EventServiceImpl;
import ru.practicum.explore.privateAPI.requests.RequestRepository;
import ru.practicum.explore.privateAPI.requests.dto.ParticipationRequestDto;
import ru.practicum.explore.privateAPI.requests.mapper.RequestMapper;
import ru.practicum.explore.privateAPI.requests.model.Request;
import ru.practicum.explore.privateAPI.requests.model.RequestStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper mapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventServiceImpl eventService;

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(String.format("Event not found %d", eventId)));
        List<Request> confirmedRequests = requestRepository.findAllByEventIdAndStatusOrderById(eventId, RequestStatus.CONFIRMED);
        event.setConfirmedRequests(confirmedRequests.size());
        validateEvent(event, userId);
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("User not found %d", userId)));
        RequestStatus status;
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            status = RequestStatus.CONFIRMED;
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else {
            status = RequestStatus.PENDING;
        }
        Request request = Request.builder()
                .requester(requester)
                .status(status)
                .event(event)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        eventRepository.save(event);
        Request savedRequest = requestRepository.save(request);
        return mapper.toDto(savedRequest);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new RequestNotFoundException(String.format("Request not found %d", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        Request savedRequest = requestRepository.save(request);
        return mapper.toDto(savedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateEvent(Event event, Long userId) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new IllegalArgumentException("Initiator of event cannot send request to participate");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new IllegalArgumentException("Request can be send only for published events");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new IllegalArgumentException("Request can be send only for events available to participate");
        }
    }
}
