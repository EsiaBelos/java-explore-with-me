package ru.practicum.explore.admin.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.explore.admin.users.model.User;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserShortDto {
    private Long id;
    private String name;
}
