package ru.practicum.explore.admin.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.exception.UserNotFoundException;
import ru.practicum.explore.admin.users.UserRepository;
import ru.practicum.explore.admin.users.dto.UserDto;
import ru.practicum.explore.admin.users.mapper.UserMapper;
import ru.practicum.explore.admin.users.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public User addUser(UserDto dto) {
        User user = repository.save(mapper.toUser(dto));
        log.debug("User saved {}", user.getId());
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(Integer from, Integer size, List<Long> ids) {
        Pageable sortedById = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id"));
        List<User> users = new ArrayList<>();
        if (ids == null) {
            users.addAll(repository.findAll(sortedById).getContent());
        } else {
            users.addAll(repository.findAllByIdIn(ids, sortedById).getContent());
        }
        return users;
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        User user = repository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        repository.deleteById(userId);
    }
}
