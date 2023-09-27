package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public final class BookingDtoWithId {

    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final String status;
    private final Long itemId;
    private final Long userId;
}
