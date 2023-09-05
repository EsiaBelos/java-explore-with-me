package ru.practicum.explore.service;

import ru.practicum.explore.EndpointHitDto;
import ru.practicum.explore.ViewStats;
import ru.practicum.explore.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface ServerService {
    EndpointHit saveHit(EndpointHitDto dto);

    List<ViewStats> getHits(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris);
}
