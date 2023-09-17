package ru.practicum.explore.privateAPI.events.service;

import ru.practicum.explore.privateAPI.events.dto.*;
import ru.practicum.explore.privateAPI.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    FullEventDto createEvent(Long userId, NewEventDto dto);

    FullEventDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest dto);

    EventRequestStatusUpdateResult updateEventRequest(Long userId, Long eventId, EventRequestStatusUpdateDto dto);

    List<ShortEventDto> getEvents(Long userId, int from, int size);

    FullEventDto getEventById(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);
}
