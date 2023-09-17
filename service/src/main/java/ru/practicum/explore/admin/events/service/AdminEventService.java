package ru.practicum.explore.admin.events.service;

import ru.practicum.explore.admin.events.dto.UpdateEventAdminRequest;
import ru.practicum.explore.privateAPI.events.dto.FullEventDto;
import ru.practicum.explore.privateAPI.events.model.State;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {
    FullEventDto updateEvent(Long eventId, UpdateEventAdminRequest dto);

    List<FullEventDto> getEvents(List<Long> users, List<State> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);
}
