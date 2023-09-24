package ru.practicum.explore.admin.compilations.service;

import ru.practicum.explore.admin.compilations.dto.CompilationDto;
import ru.practicum.explore.admin.compilations.dto.NewCompilationDto;
import ru.practicum.explore.admin.compilations.dto.UpdateCompilationRequest;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto dto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto);
}
