package ru.practicum.explore.privateAPI.users.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.explore.publicAPI.dto.Sort;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SearchSubscriptionParams {
    List<Long> users;
    List<Long> categories;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rangeStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rangeEnd;
    Boolean paid;
    Boolean onlyAvailable;
    Sort sort;
}
