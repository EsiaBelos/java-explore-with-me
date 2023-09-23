package ru.practicum.explore.admin.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.admin.categories.CatRepository;
import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.admin.events.dto.AdminStateAction;
import ru.practicum.explore.admin.events.dto.SearchParams;
import ru.practicum.explore.admin.events.dto.UpdateEventAdminRequest;
import ru.practicum.explore.exception.CategoryNotFoundException;
import ru.practicum.explore.exception.EventNotFoundException;
import ru.practicum.explore.exception.InvalidEventDateException;
import ru.practicum.explore.privateAPI.events.LocationRepository;
import ru.practicum.explore.privateAPI.events.dto.EventUtil;
import ru.practicum.explore.privateAPI.events.dto.FullEventDto;
import ru.practicum.explore.privateAPI.events.model.Event;
import ru.practicum.explore.privateAPI.events.model.Location;
import ru.practicum.explore.privateAPI.events.model.State;
import ru.practicum.explore.privateAPI.events.repository.EventRepository;
import ru.practicum.explore.privateAPI.events.repository.EventSpecification;
import ru.practicum.explore.privateAPI.mapper.EventMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final CatRepository catRepository;
    private final LocationRepository locationRepository;
    private final EventMapper mapper;

    @Override
    @Transactional
    public FullEventDto updateEvent(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException(String.format("Event not found %d", eventId)));
        if (!event.getState().equals(State.PENDING)) {
            throw new IllegalArgumentException("Only PENDING events can be updated by admin");
        }
        if (!isDateValid(event, dto)) {
            throw new InvalidEventDateException("Event Date is not valid for publication");
        }
        State state = null;
        if (dto.getStateAction() != null) {
            state = dto.getStateAction().equals(AdminStateAction.REJECT_EVENT) ? State.CANCELED : State.PUBLISHED;
        }
        Event updatedEvent = EventUtil.testForAdmin(event, dto, state);
        if (updatedEvent.getState().equals(State.PUBLISHED)) {
            updatedEvent.setPublishedOn(LocalDateTime.now());
        }
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
    public List<FullEventDto> getEvents(SearchParams params, Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        Specification<Event> specs = EventSpecification.filterForAdmin(params);
        List<Event> events = eventRepository.findAll(specs, pageable).getContent();
        if (!events.isEmpty()) {
            return events.stream()
                    .map(mapper::toFullEventDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
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
