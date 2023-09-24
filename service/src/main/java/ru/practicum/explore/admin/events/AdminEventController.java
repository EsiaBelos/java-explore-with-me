package ru.practicum.explore.admin.events;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.admin.events.dto.SearchParams;
import ru.practicum.explore.admin.events.dto.UpdateEventAdminRequest;
import ru.practicum.explore.admin.events.service.AdminEventService;
import ru.practicum.explore.privateAPI.events.dto.FullEventDto;
import ru.practicum.explore.privateAPI.events.model.State;

import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private final AdminEventService service;

    //дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
    //событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
    //событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
    @PatchMapping("/{eventId}")
    public FullEventDto updateEvent(@PathVariable Long eventId, @RequestBody @Valid UpdateEventAdminRequest dto) {
        return service.updateEvent(eventId, dto);
    }

    @GetMapping
    public List<FullEventDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<State> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {
            throw new InvalidParameterException("Date range is invalid");
        }
        return service.getEvents(new SearchParams(users, states, categories, rangeStart, rangeEnd), from, size);
    }
}
