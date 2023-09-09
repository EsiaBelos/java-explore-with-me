package ru.practicum.explore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.explore.EndpointHitDto;
import ru.practicum.explore.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface StatsMapper {
    StatsMapper INSTANCE = Mappers.getMapper(StatsMapper.class);

    @Mapping(target = "id", ignore = true)
    EndpointHit toEndpointHit(EndpointHitDto dto);
}
