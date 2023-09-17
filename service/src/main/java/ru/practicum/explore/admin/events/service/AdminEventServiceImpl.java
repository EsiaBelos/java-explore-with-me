package ru.practicum.explore.admin.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.admin.categories.CatRepository;
import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.admin.events.dto.UpdateEventAdminRequest;
import ru.practicum.explore.exception.CategoryNotFoundException;
import ru.practicum.explore.exception.EventNotFoundException;
import ru.practicum.explore.privateAPI.events.EventRepository;
import ru.practicum.explore.privateAPI.events.LocationRepository;
import ru.practicum.explore.privateAPI.events.dto.EventUtil;
import ru.practicum.explore.privateAPI.events.dto.FullEventDto;
import ru.practicum.explore.privateAPI.events.model.Event;
import ru.practicum.explore.privateAPI.events.model.Location;
import ru.practicum.explore.privateAPI.events.model.State;
import ru.practicum.explore.privateAPI.mapper.EventMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final CatRepository catRepository;
    private final LocationRepository locationRepository;
    private final EventMapper mapper;

    @Override
    public FullEventDto updateEvent(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(String.format("Event not found %d", eventId)));
        if (!event.getState().equals(State.PENDING)) {
            throw new IllegalArgumentException("Only PENDING events can be updated by admin");
        }
        if (!isDateValid(event, dto)) {
            throw new IllegalArgumentException("Event Date is not valid for publication");
        }
        Event updatedEvent = EventUtil.testForAdmin(event, dto);
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
    public List<FullEventDto> getEvents(List<Long> users, List<State> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        return null;
    }

    private Boolean isDateValid(Event event, UpdateEventAdminRequest dto) {
        LocalDateTime eventDateWithTimeLapse;
        if (dto.getEventDate() != null) {
            eventDateWithTimeLapse = dto.getEventDate().plusHours(1);
        } else {
            eventDateWithTimeLapse = event.getEventDate().plusHours(1);
        }
        return eventDateWithTimeLapse.isAfter(LocalDateTime.now());
    }

    private Category checkCategory(Long catId) {
        return catRepository.findById(catId).orElseThrow(() ->
                new CategoryNotFoundException(String.format("Category not found %d", catId)));
    }
}
