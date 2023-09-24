package ru.practicum.explore.admin.users.service;

import ru.practicum.explore.admin.users.dto.UserDto;
import ru.practicum.explore.admin.users.model.User;

import java.util.List;

public interface UserService {
    User addUser(UserDto dto);

    List<User> getUsers(Integer from, Integer size, List<Long> ids);

    void deleteUser(long userId);
}
