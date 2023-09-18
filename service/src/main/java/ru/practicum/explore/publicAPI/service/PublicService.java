package ru.practicum.explore.publicAPI.service;

import ru.practicum.explore.Sort;
import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.admin.compilations.dto.CompilationDto;
import ru.practicum.explore.privateAPI.events.dto.FullEventDto;
import ru.practicum.explore.privateAPI.events.dto.ShortEventDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface PublicService {
    CompilationDto getCompById(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    Category getCatById(Long catId);

    List<Category> getCategories(int from, int size);

    FullEventDto getEventById(Long id, HttpServletRequest request);

    List<ShortEventDto> getEvents(String text, List<Long> categories, Boolean paid,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, Sort sort,
                                  Integer from, Integer size, HttpServletRequest request);

}
