package ru.practicum.shareit.exception;

public class BookingStatusAlreadyAcceptedOrRejectedException extends RuntimeException {
    public BookingStatusAlreadyAcceptedOrRejectedException(String message) {
        super(String.format(message));
    }
}
