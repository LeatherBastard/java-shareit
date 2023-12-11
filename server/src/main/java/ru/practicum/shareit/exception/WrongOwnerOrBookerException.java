package ru.practicum.shareit.exception;

public class WrongOwnerOrBookerException extends RuntimeException {
    public WrongOwnerOrBookerException(String message) {
        super(String.format(message));
    }
}
