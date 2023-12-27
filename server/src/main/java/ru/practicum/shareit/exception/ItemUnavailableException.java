package ru.practicum.shareit.exception;

public class ItemUnavailableException extends RuntimeException {
    public ItemUnavailableException(String message, int id) {
        super(String.format(message, id));
    }
}
