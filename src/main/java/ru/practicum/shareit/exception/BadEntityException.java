package ru.practicum.shareit.exception;

public class BadEntityException extends IllegalArgumentException{

    public BadEntityException(String message) {
        super(message);
    }
}
