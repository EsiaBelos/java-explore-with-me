package ru.practicum.explore.admin.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.admin.compilations.CompilationRepository;
import ru.practicum.explore.admin.compilations.dto.CompilationDto;
import ru.practicum.explore.admin.compilations.dto.NewCompilationDto;
import ru.practicum.explore.admin.compilations.dto.UpdateCompilationRequest;
import ru.practicum.explore.admin.compilations.mapper.CompilationMapper;
import ru.practicum.explore.admin.compilations.model.Compilation;
import ru.practicum.explore.exception.CompilationNotFoundException;
import ru.practicum.explore.privateAPI.events.repository.EventRepository;
import ru.practicum.explore.privateAPI.events.dto.ShortEventDto;
import ru.practicum.explore.privateAPI.events.model.Event;
import ru.practicum.explore.privateAPI.mapper.EventMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Override
    public CompilationDto createCompilation(NewCompilationDto dto) {
        Compilation compilation = compilationMapper.toCompilation(dto);
        if (dto.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(dto.getEvents());
            if (events.size() == dto.getEvents().size()) {
                compilation.setEvents(events);
            }
        }
        Compilation savedCompilation = compilationRepository.save(compilation);
        if (compilation.getEvents() == null) {
            return compilationMapper.toCompilationDto(savedCompilation, new ArrayList<>());
        }
        return compilationMapper.toCompilationDto(savedCompilation, savedCompilation.getEvents().stream()
                .map(eventMapper::toShortEventDto)
                .collect(Collectors.toList()));
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new CompilationNotFoundException(String.format("Compilation not found %d", compId)));
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(dto.getEvents());
            if (events.size() == dto.getEvents().size()) {
                compilation.getEvents().clear();
                compilation.getEvents().addAll(events);
            }
        }
        Compilation savedCompilation = compilationRepository.save(compilation);
        List<ShortEventDto> eventDtos = savedCompilation.getEvents().stream()
                .map(eventMapper::toShortEventDto)
                .collect(Collectors.toList());
        return compilationMapper.toCompilationDto(savedCompilation, eventDtos);
    }

    @Override
    public void deleteCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new CompilationNotFoundException(String.format("Compilation not found %d", compId)));
        compilationRepository.delete(compilation);
    }

}
