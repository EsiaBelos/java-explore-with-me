package ru.practicum.explore.privateAPI.events.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.explore.privateAPI.requests.dto.ParticipationRequestDto;

import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;
}
