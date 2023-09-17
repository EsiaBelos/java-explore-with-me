package ru.practicum.explore.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({UserNotFoundException.class, CategoryNotFoundException.class, EventNotFoundException.class,
    RequestNotFoundException.class, CompilationNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(final RuntimeException e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.NOT_FOUND)
                        .reason("The required object was not found.")
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .build());
    }

    @ExceptionHandler({InvalidCategoryForDeleteException.class})
    public ResponseEntity<ApiError> handleInvalidCat(final IllegalArgumentException e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.CONFLICT)
                        .reason("For the requested operation the conditions are not met.")
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .build());
    }

}
