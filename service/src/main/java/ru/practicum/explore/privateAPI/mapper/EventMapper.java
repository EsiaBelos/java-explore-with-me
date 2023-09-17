package ru.practicum.explore.privateAPI.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.admin.users.mapper.UserMapper;
import ru.practicum.explore.admin.users.model.User;
import ru.practicum.explore.privateAPI.events.dto.FullEventDto;
import ru.practicum.explore.privateAPI.events.dto.NewEventDto;
import ru.practicum.explore.privateAPI.events.dto.ShortEventDto;
import ru.practicum.explore.privateAPI.events.model.Event;
import ru.practicum.explore.privateAPI.events.model.Location;
import ru.practicum.explore.privateAPI.events.model.State;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "created", target = "createdOn", dateFormat = "dd-MM-yyyy HH:mm:ss")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    Event toEvent(NewEventDto dto, LocalDateTime created, State state,
                  User initiator, Category category, Location location);

    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    FullEventDto toFullEventDto(Event event);

    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    ShortEventDto toShortEventDto(Event event);
}
