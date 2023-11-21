package ru.practicum.shareit.exception;

public class BookingForCommentNotFoundException extends RuntimeException {
    public BookingForCommentNotFoundException(String message, int id) {
        super(String.format(message, id));
    }
}
