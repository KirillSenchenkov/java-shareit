package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
public final class BookItemRequestDto {
    private final long itemId;
    @FutureOrPresent
    private final LocalDateTime start;
    @Future
    private final LocalDateTime end;
}