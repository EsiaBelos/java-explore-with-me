package ru.practicum.explore.publicAPI.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.EndpointHitDto;
import ru.practicum.explore.StatsClient;
import ru.practicum.explore.ViewStats;
import ru.practicum.explore.admin.categories.CatRepository;
import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.admin.compilations.CompilationRepository;
import ru.practicum.explore.admin.compilations.dto.CompilationDto;
import ru.practicum.explore.admin.compilations.mapper.CompilationMapper;
import ru.practicum.explore.admin.compilations.model.Compilation;
import ru.practicum.explore.exception.CategoryNotFoundException;
import ru.practicum.explore.exception.CompilationNotFoundException;
import ru.practicum.explore.exception.EventNotFoundException;
import ru.practicum.explore.exception.InvalidDateRangeException;
import ru.practicum.explore.privateAPI.events.dto.FullEventDto;
import ru.practicum.explore.privateAPI.events.dto.ShortEventDto;
import ru.practicum.explore.privateAPI.events.model.Event;
import ru.practicum.explore.privateAPI.events.model.State;
import ru.practicum.explore.privateAPI.events.repository.EventRepository;
import ru.practicum.explore.privateAPI.events.repository.EventSpecification;
import ru.practicum.explore.privateAPI.mapper.EventMapper;
import ru.practicum.explore.privateAPI.requests.RequestRepository;
import ru.practicum.explore.privateAPI.requests.model.Request;
import ru.practicum.explore.privateAPI.requests.model.RequestStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
    private final RequestRepository requestRepository;
    private final String artifact = "explore-with-me-service";

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Category getCatById(Long catId) {
        return catRepository.findById(catId).orElseThrow(() ->
                new CategoryNotFoundException(String.format("Category not found %d", catId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategories(int from, int size) {
        Pageable sortedById = PageRequest.of(from, size, Sort.by("id"));
        return catRepository.findAll(sortedById).getContent();
    }

    @Override
    @Transactional
    public FullEventDto getEventById(Long id, HttpServletRequest request) {
        saveStats(request);
        Event event = eventRepository.findById(id).orElseThrow(() ->
                new EventNotFoundException(String.format("Event not found %d", id)));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EventNotFoundException(String.format("Even must be published %d", id));
        }
        List<Request> confirmedRequests = requestRepository.findAllByEventIdAndStatusOrderById(id, RequestStatus.CONFIRMED);
        event.setConfirmedRequests(confirmedRequests.size());
        LocalDateTime startDateTime = LocalDateTime.now().minusYears(5).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime endDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ResponseEntity<List<ViewStats>> response = statsClient.getHits(startDateTime,
                endDateTime, false, List.of("/events/" + id));
        if (response.getBody() != null && response.getBody().size() != 0) {
            Long hits = response.getBody().get(0).getHits();
            event.setViews(hits);
        }
        eventRepository.save(event);
        return eventMapper.toFullEventDto(event);
    }

    @Override
    public List<ShortEventDto> getEvents(String text, List<Long> categories, Boolean paid,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                         ru.practicum.explore.Sort sort, Integer from, Integer size,
                                         HttpServletRequest httpServletRequest) {
        saveStats(httpServletRequest);
        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {
            throw new InvalidDateRangeException("Date range is invalid");
        }
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        if (sort != null) {
            switch (sort) {
                case EVENT_DATE:
                    pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "eventDate"));
                    break;
                case VIEWS:
                    pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "views"));
                    break;
            }
        }
        Specification<Event> specs = EventSpecification.filterForPublic(text, categories, paid, rangeStart, rangeEnd, State.PUBLISHED);
        List<Event> events = eventRepository.findAll(specs, pageable).getContent();
        if (!events.isEmpty()) {
            saveStatsForList(httpServletRequest, events.stream().map(Event::getId).collect(Collectors.toSet()));
            if (onlyAvailable != null && onlyAvailable) {
                return events.stream()
                        .map(eventMapper::toShortEventDto)
                        .filter(event -> event.getConfirmedRequests() < event.getParticipantLimit())
                        .collect(Collectors.toList());
            }
            return events.stream()
                    .map(eventMapper::toShortEventDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void saveStatsForList(HttpServletRequest request, Set<Long> eventIds) {
        List<EndpointHitDto> dtos = eventIds.stream()
                .map(id -> createEndpointHitDto(request.getRemoteAddr(), "/events/" + id,
                        LocalDateTime.now()))
                .collect(Collectors.toList());
        dtos.forEach(statsClient::saveHit);
    }

    private void saveStats(HttpServletRequest request) {
        statsClient.saveHit(createEndpointHitDto(request.getRemoteAddr(), request.getRequestURI(), LocalDateTime.now()));
        log.info("Stats saved for request URI: {}", request.getRequestURI());
    }

    private EndpointHitDto createEndpointHitDto(String ip, String uri, LocalDateTime timestamp) {
        return EndpointHitDto.builder()
                .ip(ip)
                .uri(uri)
                .timestamp(timestamp)
                .app(artifact)
                .build();
    }
}
