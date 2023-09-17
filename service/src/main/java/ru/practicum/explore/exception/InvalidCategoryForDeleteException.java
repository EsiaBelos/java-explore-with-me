package ru.practicum.explore.exception;

public class InvalidCategoryForDeleteException extends IllegalArgumentException {
    public InvalidCategoryForDeleteException(String message) {
        super(message);
    }
}
