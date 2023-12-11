package ru.practicum.shareit.exception;

public class BookingDateValidationException extends RuntimeException {
    public BookingDateValidationException(String message) {
        super(String.format(message));
    }
}
