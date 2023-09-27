package ru.practicum.explore.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({UserNotFoundException.class, CategoryNotFoundException.class, EventNotFoundException.class,
            RequestNotFoundException.class, CompilationNotFoundException.class, SubscriptionNotFoundException.class})
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

    @ExceptionHandler({InvalidCategoryForDeleteException.class, IllegalArgumentException.class})
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

    @ExceptionHandler({InvalidDateRangeException.class, InvalidEventDateException.class})
    public ResponseEntity<ApiError> handleDateTimeExc(final RuntimeException e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.BAD_REQUEST)
                        .reason("Incorrectly made request.")
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
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

    @ExceptionHandler
    public ResponseEntity<ApiError> handleThrowable(final Throwable e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder()
                        .errors(List.of(Arrays.toString(e.getStackTrace())))
                        .message(e.getMessage())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .reason("Unhandled exception was generated during execution of the current request.")
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.BAD_REQUEST)
                        .reason("Incorrectly made request.")
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(final MissingServletRequestParameterException e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.BAD_REQUEST)
                        .reason("Required request parameter is missing")
                        .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .build());
    }

}
