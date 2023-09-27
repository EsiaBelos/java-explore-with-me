package ru.practicum.explore.admin.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.explore.admin.users.dto.UserDto;
import ru.practicum.explore.admin.users.dto.UserShortDto;
import ru.practicum.explore.admin.users.model.User;
import ru.practicum.explore.privateAPI.users.dto.UserSubscriptionDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    User toUser(UserDto dto);

    UserShortDto toUserShortDto(User user);

    @Mapping(target = "subscriptions", source = "subscriptions")
    UserSubscriptionDto toUserSubscriptionDto(User user, List<UserShortDto> subscriptions);
}
