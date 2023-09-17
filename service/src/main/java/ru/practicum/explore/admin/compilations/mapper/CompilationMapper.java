package ru.practicum.explore.admin.compilations.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore.admin.compilations.dto.CompilationDto;
import ru.practicum.explore.admin.compilations.dto.NewCompilationDto;
import ru.practicum.explore.admin.compilations.model.Compilation;
import ru.practicum.explore.privateAPI.events.dto.ShortEventDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    Compilation toCompilation(NewCompilationDto dto);

    @Mapping(target = "events", source = "events")
    CompilationDto toCompilationDto(Compilation compilation, List<ShortEventDto> events);
}
