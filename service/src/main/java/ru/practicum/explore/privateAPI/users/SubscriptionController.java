package ru.practicum.explore.privateAPI.users;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.admin.users.dto.UserShortDto;
import ru.practicum.explore.exception.InvalidDateRangeException;
import ru.practicum.explore.privateAPI.events.dto.ShortEventDto;
import ru.practicum.explore.privateAPI.users.dto.SearchSubscriptionParams;
import ru.practicum.explore.privateAPI.users.dto.UserSubscriptionDto;
import ru.practicum.explore.privateAPI.users.service.SubscriptionService;
import ru.practicum.explore.publicAPI.dto.Sort;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;

    @PostMapping("/{subscribeToId}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserSubscriptionDto addSubscription(@PathVariable Long userId, @PathVariable Long subscribeToId) {
        return service.addSubscription(userId, subscribeToId);
    }

    @DeleteMapping("/{subscribeToId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscription(@PathVariable Long userId, @PathVariable Long subscribeToId) {
        service.deleteSubscription(userId, subscribeToId);
    }

    @GetMapping
    public List<UserShortDto> getSubscriptions(@PathVariable Long userId) {
        return service.getSubscriptions(userId);
    }

    @GetMapping("/events")
    public List<ShortEventDto> getEventsBySubscription(@PathVariable Long userId, @RequestParam Boolean onlyAvailable,
                                                       @RequestParam(required = false) List<Long> categories,
                                                       @RequestParam(required = false) Boolean paid,
                                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                       @RequestParam(required = false) Sort sort,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(defaultValue = "10") @Positive Integer size) {
        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {
            throw new InvalidDateRangeException("Date range is invalid");
        }
        SearchSubscriptionParams params = SearchSubscriptionParams.builder().rangeStart(rangeStart).rangeEnd(rangeEnd)
                .categories(categories).onlyAvailable(onlyAvailable).sort(sort).paid(paid).build();
        return service.getEventsBySubscription(userId, params, from, size);
    }

    @GetMapping("/subscribers")
    public List<UserShortDto> getSubscribers(@PathVariable Long userId) {
        return service.getSubscribers(userId);
    }
}
