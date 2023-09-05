package ru.practicum.explore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long>  {

    @Query(value = "select new ru.practicum.explore.ViewStats(eh.app, eh.uri, count(eh.ip)) " +
            "from EndpointHit eh " +
            "where eh.uri in :uris and eh.timestamp > :start and eh.timestamp < :end " +
            "group by eh.uri, eh.app " +
            "order by count (eh.ip) desc")
    List<ViewStats> findAllByUrisAndNotUniqueIp(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query(value = "select new ru.practicum.explore.ViewStats(eh.app, eh.uri, count(eh.ip)) " +
            "from EndpointHit eh " +
            "where eh.timestamp > :start and eh.timestamp < :end " +
            "group by eh.uri, eh.app " +
            "order by count (eh.ip) desc")
    List<ViewStats> findAllWithNoUrisAndNotUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query(value = "select new ru.practicum.explore.ViewStats(eh.app, eh.uri, count(distinct eh.ip)) " +
            "from EndpointHit eh " +
            "where eh.uri in :uris and eh.timestamp > :start and eh.timestamp < :end " +
            "group by eh.uri, eh.app " +
            "order by count (eh.ip) desc")
    List<ViewStats> findAllByUrisAndUniqueIp(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query(value = "select new ru.practicum.explore.ViewStats(eh.app, eh.uri, count(distinct eh.ip)) " +
            "from EndpointHit eh " +
            "where eh.timestamp > :start and eh.timestamp < :end " +
            "group by eh.uri, eh.app " +
            "order by count (eh.ip) desc")
    List<ViewStats> findAllWithNoUrisAndUniqueIp(LocalDateTime start, LocalDateTime end);
}
