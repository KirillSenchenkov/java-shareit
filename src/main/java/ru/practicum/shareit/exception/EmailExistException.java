package ru.practicum.shareit.exception;

public class EmailExistException extends IllegalArgumentException {

    public EmailExistException(String message) {
        super(message);
    }
}
