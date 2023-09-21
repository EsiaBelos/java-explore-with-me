package ru.practicum.explore.privateAPI.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.privateAPI.requests.dto.ParticipationRequestDto;
import ru.practicum.explore.privateAPI.requests.model.Request;
import ru.practicum.explore.privateAPI.requests.model.RequestStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    List<Request> findAllByEventIdAndStatusOrderById(Long eventId, RequestStatus requestStatus);

    @Query(value = "select new ru.practicum.explore.privateAPI.requests.dto.ParticipationRequestDto(" +
            "r.id, r.created, r.event.id, r.requester.id, r.status) " +
            "from Request r " +
            "where r.event.id = :eventId " +
            "order by r.id")
    List<ParticipationRequestDto> findAllByEventId(Long eventId);

    List<Request> findAllByEventIdInAndStatusOrderById(Set<Long> longs, RequestStatus requestStatus);
}
