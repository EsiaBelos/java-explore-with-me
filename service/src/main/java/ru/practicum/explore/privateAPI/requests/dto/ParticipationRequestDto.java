package ru.practicum.explore.privateAPI.requests.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.privateAPI.requests.model.RequestStatus;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private RequestStatus status;

    public ParticipationRequestDto(Long id, LocalDateTime created, Long event, Long requester, RequestStatus status) {
        this.id = id;
        this.created = created;
        this.event = event;
        this.requester = requester;
        this.status = status;
    }
}
