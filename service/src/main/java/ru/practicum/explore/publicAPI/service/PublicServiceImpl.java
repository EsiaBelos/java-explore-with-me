package ru.practicum.explore.publicAPI.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.explore.admin.categories.CatRepository;
import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.admin.compilations.CompilationRepository;
import ru.practicum.explore.admin.compilations.dto.CompilationDto;
import ru.practicum.explore.admin.compilations.mapper.CompilationMapper;
import ru.practicum.explore.admin.compilations.model.Compilation;
import ru.practicum.explore.exception.CategoryNotFoundException;
import ru.practicum.explore.exception.CompilationNotFoundException;
import ru.practicum.explore.privateAPI.events.EventRepository;
import ru.practicum.explore.privateAPI.events.dto.ShortEventDto;
import ru.practicum.explore.privateAPI.mapper.EventMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicServiceImpl implements PublicService {

    private final CatRepository catRepository;
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Override
    public CompilationDto getCompById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new CompilationNotFoundException(String.format("Compilation not found %d", compId)));
        if (compilation.getEvents().isEmpty()) {
            return compilationMapper.toCompilationDto(compilation, new ArrayList<>());
        }
        List<ShortEventDto> eventDtos = compilation.getEvents().stream()
                .map(eventMapper::toShortEventDto)
                .collect(Collectors.toList());
        return compilationMapper.toCompilationDto(compilation, eventDtos);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable sortedById = PageRequest.of(from, size, Sort.by("id"));
        List<Compilation> compilations = new ArrayList<>();
        if (pinned != null) {
            compilations.addAll(compilationRepository.findAllByPinned(pinned, sortedById));
        } else {
            compilations.addAll(compilationRepository.findAll(sortedById).getContent());
        }
        if (compilations.isEmpty()) {
            return Collections.emptyList();
        }
        return compilations.stream()
                .map(compilation -> compilationMapper.toCompilationDto(compilation, compilation.getEvents().stream()
                        .map(eventMapper::toShortEventDto)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public Category getCatById(Long catId) {
        return catRepository.findById(catId).orElseThrow(() ->
                new CategoryNotFoundException(String.format("Category not found %d", catId)));
    }

    @Override
    public List<Category> getCategories(int from, int size) {
        Pageable sortedById = PageRequest.of(from, size, Sort.by("id"));
        return catRepository.findAll(sortedById).getContent();
    }
}
