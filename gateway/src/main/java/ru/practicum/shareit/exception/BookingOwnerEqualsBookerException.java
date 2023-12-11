package ru.practicum.shareit.exception;

public class BookingOwnerEqualsBookerException extends RuntimeException {
    public BookingOwnerEqualsBookerException(String message) {
        super(String.format(message));
    }
}
