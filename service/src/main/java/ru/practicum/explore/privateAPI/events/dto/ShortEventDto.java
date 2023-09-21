package ru.practicum.explore.privateAPI.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.explore.admin.categories.model.Category;
import ru.practicum.explore.admin.users.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@NoArgsConstructor
public class ShortEventDto {
    private Long id;
    private String title;
    private String annotation; //краткое опис

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента

    private Boolean paid; //Нужно ли оплачивать участие
    private Boolean requestModeration; //Нужна ли пре-модерация заявок на участие

    private Integer participantLimit;
    private Integer views;
    private Integer confirmedRequests;

    private Category category;
    private UserShortDto initiator;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortEventDto that = (ShortEventDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
