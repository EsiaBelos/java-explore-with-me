package ru.practicum.explore.privateAPI.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.privateAPI.events.dto.ShortEventDto;
import ru.practicum.explore.privateAPI.events.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findAllByCategoryId(long catId);

    List<Event> findAllByIdIn(List<Long> events);

    List<Event> findAllByInitiatorId(Long userId, Pageable sortedById);
}
