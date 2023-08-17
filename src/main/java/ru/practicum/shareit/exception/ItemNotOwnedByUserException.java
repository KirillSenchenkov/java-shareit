package ru.practicum.shareit.exception;

public class ItemNotOwnedByUserException extends IllegalArgumentException {

    public ItemNotOwnedByUserException (String message) {
        super(message);
    }
}
