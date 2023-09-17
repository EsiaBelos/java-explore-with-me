package ru.practicum.explore.privateAPI.events;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.privateAPI.events.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByCategoryId(long catId);

    List<Event> findAllByIdIn(List<Long> events);
}
