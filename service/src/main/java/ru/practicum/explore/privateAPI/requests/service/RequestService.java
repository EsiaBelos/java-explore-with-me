package ru.practicum.explore.privateAPI.requests.service;

import ru.practicum.explore.privateAPI.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequests(Long userId);
}
