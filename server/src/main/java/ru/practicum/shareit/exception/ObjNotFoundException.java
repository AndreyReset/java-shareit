package ru.practicum.shareit.exception;

public class ObjNotFoundException extends RuntimeException {
    public ObjNotFoundException(String message) {
        super(message);
    }
}
