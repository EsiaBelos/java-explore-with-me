package ru.practicum.explore.privateAPI.events.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.explore.privateAPI.requests.model.RequestStatus;

import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class EventRequestStatusUpdateDto {
    private List<Long> requestIds;
    private RequestStatus status;
}
