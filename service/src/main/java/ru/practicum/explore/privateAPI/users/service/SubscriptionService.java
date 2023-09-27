package ru.practicum.explore.privateAPI.users.service;

import ru.practicum.explore.admin.users.dto.UserShortDto;
import ru.practicum.explore.privateAPI.events.dto.ShortEventDto;
import ru.practicum.explore.privateAPI.users.dto.SearchSubscriptionParams;
import ru.practicum.explore.privateAPI.users.dto.UserSubscriptionDto;

import java.util.List;

public interface SubscriptionService {
    UserSubscriptionDto addSubscription(Long userId, Long subscribeToId);

    void deleteSubscription(Long userId, Long subscrId);

    List<UserShortDto> getSubscriptions(Long userId);

    List<ShortEventDto> getEventsBySubscription(Long userId, SearchSubscriptionParams params, Integer from, Integer size);

    List<UserShortDto> getSubscribers(Long userId);
}
