package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.EndpointHitDto;
import ru.practicum.explore.StatsRepository;
import ru.practicum.explore.ViewStats;
import ru.practicum.explore.mapper.StatsMapper;
import ru.practicum.explore.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerServiceImpl implements ServerService {

    private final StatsRepository repository;
    private final StatsMapper mapper;

    @Override
    @Transactional
    public EndpointHit saveHit(EndpointHitDto dto) {
        EndpointHit hit = mapper.toEndpointHit(dto);
        EndpointHit endpointHit = repository.save(hit);
        log.info("Hit saved {}", endpointHit.getUri());
        return endpointHit;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStats> getHits(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris) {
        if (uris != null) {
            if (!unique) {
                return repository.findAllByUrisAndNotUniqueIp(uris, start, end);
            } else {
                return repository.findAllByUrisAndUniqueIp(uris, start, end);
            }
        }
        if (unique) {
            return repository.findAllWithNoUrisAndUniqueIp(start, end);
        }
        return repository.findAllWithNoUrisAndNotUniqueIp(start, end);
    }
}
