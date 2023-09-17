package ru.practicum.explore.publicAPI.service;

import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.admin.compilations.dto.CompilationDto;

import java.util.List;

public interface PublicService {
    CompilationDto getCompById(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    Category getCatById(Long catId);

    List<Category> getCategories(int from, int size);
}
