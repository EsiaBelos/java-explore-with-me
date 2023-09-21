package ru.practicum.explore.privateAPI.events;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.privateAPI.events.dto.*;
import ru.practicum.explore.privateAPI.events.service.EventService;
import ru.practicum.explore.privateAPI.requests.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventController {

    private final EventService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FullEventDto createEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto dto) {
        return service.createEvent(userId, dto);
    }

    @PatchMapping("/{eventId}") //only PENDING or CANCELED
    public FullEventDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest dto) {
        return service.updateEvent(userId, eventId, dto);
    }

    @PatchMapping("/{eventId}/requests") //only PENDING
    public EventRequestStatusUpdateResult updateEventRequest(@PathVariable Long userId, @PathVariable Long eventId,
                                    @RequestBody EventRequestStatusUpdateDto dto) {
        return service.updateEventRequest(userId, eventId, dto);
    }

    @GetMapping //events created by the user
    public List<ShortEventDto> getEvents(@PathVariable Long userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        return service.getEvents(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public FullEventDto getEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getEventById(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        return service.getRequests(userId, eventId);
    }
}
