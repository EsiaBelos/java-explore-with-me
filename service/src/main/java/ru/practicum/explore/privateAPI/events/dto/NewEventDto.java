package ru.practicum.explore.privateAPI.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import ru.practicum.explore.privateAPI.events.annotation.EventDateValid;
import ru.practicum.explore.privateAPI.events.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Validated
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation; //краткое опис

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description; //полное опис

    @NotNull
    @EventDateValid
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента

    private Boolean paid = false; //Нужно ли оплачивать участие
    private Boolean requestModeration = true; //Нужна ли пре-модерация заявок на участие

    private Integer participantLimit = 0;
    @NotNull
    private Location location;
    @NotNull
    @Positive
    private Long category;

}
