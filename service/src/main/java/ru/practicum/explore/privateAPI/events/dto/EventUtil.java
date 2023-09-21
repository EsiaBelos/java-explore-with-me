package ru.practicum.explore.privateAPI.events.dto;

import ru.practicum.explore.admin.events.dto.AdminStateAction;
import ru.practicum.explore.admin.events.dto.UpdateEventAdminRequest;
import ru.practicum.explore.privateAPI.events.model.Event;
import ru.practicum.explore.privateAPI.events.model.State;

import java.util.function.Consumer;

public class EventUtil {

    public static <T> void setIfNonNull(final Consumer<T> tConsumer, final T value) {
        if (value != null) {
            tConsumer.accept(value);
        }
    }

    public static Event test(Event event, UpdateEventUserRequest dto, State state) {
        setIfNonNull(event::setTitle, dto.getTitle());
        setIfNonNull(event::setAnnotation, dto.getAnnotation());
        setIfNonNull(event::setDescription, dto.getDescription());
        setIfNonNull(event::setEventDate, dto.getEventDate());
        setIfNonNull(event::setPaid, dto.getPaid());
        setIfNonNull(event::setRequestModeration, dto.getRequestModeration());
        setIfNonNull(event::setParticipantLimit, dto.getParticipantLimit());
        setIfNonNull(event::setState, state);
        return event;
    }

    public static Event testForAdmin(Event event, UpdateEventAdminRequest dto, State state) {
        setIfNonNull(event::setTitle, dto.getTitle());
        setIfNonNull(event::setAnnotation, dto.getAnnotation());
        setIfNonNull(event::setDescription, dto.getDescription());
        setIfNonNull(event::setEventDate, dto.getEventDate());
        setIfNonNull(event::setPaid, dto.getPaid());
        setIfNonNull(event::setRequestModeration, dto.getRequestModeration());
        setIfNonNull(event::setParticipantLimit, dto.getParticipantLimit());
        setIfNonNull(event::setState, state);
        return event;
    }
}
