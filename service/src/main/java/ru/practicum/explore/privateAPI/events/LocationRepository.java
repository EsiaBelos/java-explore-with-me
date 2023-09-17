package ru.practicum.explore.privateAPI.events;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.privateAPI.events.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
