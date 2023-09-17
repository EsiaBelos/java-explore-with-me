package ru.practicum.explore.privateAPI.events.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<EventDateValid, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime date, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime dateWithTimeLapse = date.plusHours(2);
        return dateWithTimeLapse.isAfter(LocalDateTime.now());
    }
}
