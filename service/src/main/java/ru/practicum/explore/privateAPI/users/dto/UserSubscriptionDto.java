package ru.practicum.explore.privateAPI.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.explore.admin.users.dto.UserShortDto;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserSubscriptionDto {
    private Long id;
    private String name;
    private List<UserShortDto> subscriptions;
}
