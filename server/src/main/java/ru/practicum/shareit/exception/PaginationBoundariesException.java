package ru.practicum.shareit.exception;

public class PaginationBoundariesException extends RuntimeException {
    public PaginationBoundariesException(int from, int size) {
        super(String.format("Cannot display entities with %d from and %d size parameters", from, size));
    }

}
