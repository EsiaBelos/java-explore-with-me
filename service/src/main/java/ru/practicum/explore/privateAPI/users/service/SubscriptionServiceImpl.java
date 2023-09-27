package ru.practicum.explore.privateAPI.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.admin.users.UserRepository;
import ru.practicum.explore.admin.users.dto.UserShortDto;
import ru.practicum.explore.admin.users.mapper.UserMapper;
import ru.practicum.explore.admin.users.model.User;
import ru.practicum.explore.exception.SubscriptionNotFoundException;
import ru.practicum.explore.exception.UserNotFoundException;
import ru.practicum.explore.privateAPI.events.dto.ShortEventDto;
import ru.practicum.explore.privateAPI.events.model.Event;
import ru.practicum.explore.privateAPI.events.model.State;
import ru.practicum.explore.privateAPI.events.repository.EventRepository;
import ru.practicum.explore.privateAPI.events.repository.EventSpecification;
import ru.practicum.explore.privateAPI.mapper.EventMapper;
import ru.practicum.explore.privateAPI.users.dto.SearchSubscriptionParams;
import ru.practicum.explore.privateAPI.users.dto.UserSubscriptionDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;


    @Override
    @Transactional
    public UserSubscriptionDto addSubscription(Long userId, Long subscribeToId) {
        User user = getUser(userId);
        User subscription = getUser(subscribeToId);
        user.getSubscriptions().add(subscription);
        User savedUser = userRepository.save(user);
        log.info("Subscription to {} saved for user {}", subscribeToId, userId);
        return userMapper.toUserSubscriptionDto(savedUser, savedUser.getSubscriptions().stream()
                .map(userMapper::toUserShortDto).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public void deleteSubscription(Long userId, Long subscribeToId) {
        User user = getUser(userId);
        User subscription = getUser(subscribeToId);
        if (!user.getSubscriptions().contains(subscription)) {
            throw new SubscriptionNotFoundException(String.format("Subscription to %d not found for user %d", subscribeToId, userId));
        }
        user.getSubscriptions().remove(subscription);
        userRepository.save(user);
    }

    @Override
    public List<UserShortDto> getSubscriptions(Long userId) {
        User user = getUser(userId);
        List<User> subscriptions = user.getSubscriptions();
        return subscriptions.isEmpty() ? new ArrayList<>() : subscriptions.stream().map(userMapper::toUserShortDto).collect(Collectors.toList());
    }

    @Override
    public List<ShortEventDto> getEventsBySubscription(Long userId, SearchSubscriptionParams params, Integer from, Integer size) {
        User user = getUser(userId);
        List<Long> subscriptions = user.getSubscriptions().stream().map(User::getId).collect(Collectors.toList());
        if (subscriptions.isEmpty()) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        if (params.getSort() != null) {
            switch (params.getSort()) {
                case EVENT_DATE:
                    pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "eventDate"));
                    break;
                case VIEWS:
                    pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "views"));
                    break;
            }
        }
        params.setUsers(subscriptions);
        Specification<Event> specification = EventSpecification.filterForSubscriptions(params, State.PUBLISHED);
        List<Event> events = eventRepository.findAll(specification, pageable).getContent();
        return events.isEmpty() ? new ArrayList<>() : events.stream().map(eventMapper::toShortEventDto).collect(Collectors.toList());
    }

    @Override
    public List<UserShortDto> getSubscribers(Long userId) {
        User user = getUser(userId);
        List<User> subscribers = user.getSubscribers();
        return subscribers.isEmpty() ? new ArrayList<>() : subscribers.stream().map(userMapper::toUserShortDto).collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("User not found %d", userId)));
    }
}
