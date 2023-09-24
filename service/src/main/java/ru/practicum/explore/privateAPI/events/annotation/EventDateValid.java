package ru.practicum.explore.privateAPI.events.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = EventDateValidator.class)
@Documented
public @interface EventDateValid {
    String message() default "{DateTime is invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
