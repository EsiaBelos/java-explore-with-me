package ru.practicum.explore.admin.events.service;

import ru.practicum.explore.admin.events.dto.SearchParams;
import ru.practicum.explore.admin.events.dto.UpdateEventAdminRequest;
import ru.practicum.explore.privateAPI.events.dto.FullEventDto;

import java.util.List;

public interface AdminEventService {
    FullEventDto updateEvent(Long eventId, UpdateEventAdminRequest dto);

    List<FullEventDto> getEvents(SearchParams params, Integer from, Integer size);
}
