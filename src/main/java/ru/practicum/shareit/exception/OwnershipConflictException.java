package ru.practicum.shareit.exception;

public class OwnershipConflictException extends RuntimeException {
    public OwnershipConflictException(String message) {
        super(message);
    }
}
