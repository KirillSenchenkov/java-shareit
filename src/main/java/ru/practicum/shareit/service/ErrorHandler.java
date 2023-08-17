package ru.practicum.shareit.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.ItemNotOwnedByUserException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Map;



@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({ItemNotOwnedByUserException.class, NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final RuntimeException e) {
        return Map.of("error", e.getMessage());
    }
}
