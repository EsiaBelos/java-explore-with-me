package ru.practicum.explore.admin.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore.admin.users.dto.UserDto;
import ru.practicum.explore.admin.users.dto.UserShortDto;
import ru.practicum.explore.admin.users.model.User;
import ru.practicum.explore.privateAPI.users.dto.UserSubscriptionDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    @Mapping(target = "subscribers", ignore = true)
    User toUser(UserDto dto);

    UserShortDto toUserShortDto(User user);

    @Mapping(target = "subscriptions", source = "subscriptions")
    UserSubscriptionDto toUserSubscriptionDto(User user, List<UserShortDto> subscriptions);
}
