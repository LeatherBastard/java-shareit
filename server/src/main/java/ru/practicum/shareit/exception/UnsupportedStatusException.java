package ru.practicum.shareit.exception;

public class UnsupportedStatusException extends RuntimeException {
    public UnsupportedStatusException(String message, String status) {
        super(String.format(message, status));
    }
}
