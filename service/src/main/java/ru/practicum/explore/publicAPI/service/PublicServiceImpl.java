package ru.practicum.explore.publicAPI.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.explore.EndpointHitDto;
import ru.practicum.explore.StatsClient;
import ru.practicum.explore.admin.categories.CatRepository;
import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.admin.compilations.CompilationRepository;
import ru.practicum.explore.admin.compilations.dto.CompilationDto;
import ru.practicum.explore.admin.compilations.mapper.CompilationMapper;
import ru.practicum.explore.admin.compilations.model.Compilation;
import ru.practicum.explore.exception.CategoryNotFoundException;
import ru.practicum.explore.exception.CompilationNotFoundException;
import ru.practicum.explore.privateAPI.events.EventRepository;
import ru.practicum.explore.privateAPI.events.dto.FullEventDto;
import ru.practicum.explore.privateAPI.events.dto.ShortEventDto;
import ru.practicum.explore.privateAPI.mapper.EventMapper;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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
    private final StatsClient statsClient;

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

    @Override
    public FullEventDto getEventById(Long id, HttpServletRequest request) {
        saveStats(request);
        //событие должно быть опубликовано
        //информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        return null;
    }

    @Override
    public List<ShortEventDto> getEvents(String text, List<Long> categories, Boolean paid,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                         ru.practicum.explore.Sort sort, Integer from, Integer size,
                                         HttpServletRequest request) {
        saveStats(request);
        //это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
        //текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
        //если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
        //информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        return null;
    }

    private void saveStats(HttpServletRequest request) {
        String artifact = "explore-with-me-service";
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .app(artifact)
                .build();
        statsClient.saveHit(hitDto);
        log.info("Stats saved for request URI: {}", request.getRequestURI());
    }
}
