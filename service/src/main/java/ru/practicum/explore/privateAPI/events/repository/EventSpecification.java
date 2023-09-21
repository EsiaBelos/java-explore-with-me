package ru.practicum.explore.privateAPI.events.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.admin.users.model.User;
import ru.practicum.explore.privateAPI.events.model.Event;
import ru.practicum.explore.privateAPI.events.model.Event_;
import ru.practicum.explore.privateAPI.events.model.State;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;

public class EventSpecification {
    public static Specification<Event> filterForPublic(String text, List<Long> categories, Boolean paid,
                                                       LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                       State state) {
        return ((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (text != null) {
                String search = "%" + text.toUpperCase() + "%";
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.upper(root.get(Event_.ANNOTATION)), search),
                        criteriaBuilder.like(criteriaBuilder.upper(root.get(Event_.DESCRIPTION)), search)
                ));
            }
            ;
            if (categories != null) {
                Join<Event, Category> join = root.join(Event_.CATEGORY, JoinType.INNER);
                predicate = criteriaBuilder.and(predicate, join.get("id").in(categories));
            }
            if (paid != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(Event_.PAID), paid));
            }
            if (state != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(Event_.STATE), state));
            }
            if (rangeStart != null && rangeEnd == null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.EVENT_DATE), rangeStart));
            }
            if (rangeEnd != null && rangeStart == null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get(Event_.EVENT_DATE), LocalDateTime.now(), rangeEnd)
                );
            }
            if (rangeEnd != null && rangeStart != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get(Event_.EVENT_DATE), rangeStart, rangeEnd)
                );
            }
            if (rangeEnd == null && rangeStart == null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.EVENT_DATE), LocalDateTime.now()));
            }
            return predicate;
        });
    }

    public static Specification<Event> filterForAdmin(List<Long> users, List<State> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        return ((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (categories != null) {
                Join<Event, Category> join = root.join(Event_.CATEGORY, JoinType.INNER);
                predicate = criteriaBuilder.and(predicate, join.get("id").in(categories));
            }
            if (users != null) {
                Join<Event, User> join = root.join(Event_.INITIATOR, JoinType.INNER);
                predicate = criteriaBuilder.and(predicate, join.get("id").in(users));
            }
            if (states != null) {
                predicate = criteriaBuilder.and(predicate, root.get(Event_.STATE).in(states));
            }
            if (rangeStart != null && rangeEnd == null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.EVENT_DATE), rangeStart));
            }
            if (rangeEnd != null && rangeStart == null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get(Event_.EVENT_DATE), LocalDateTime.now(), rangeEnd)
                );
            }
            if (rangeEnd != null && rangeStart != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get(Event_.EVENT_DATE), rangeStart, rangeEnd)
                );
            }
            if (rangeEnd == null && rangeStart == null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.EVENT_DATE), LocalDateTime.now()));
            }
            return predicate;
        });
    }
}
