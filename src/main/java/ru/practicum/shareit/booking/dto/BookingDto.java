package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
public final class BookingDto {

    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final BookingStatus status;
    private final ItemDto item;
    private final UserDto booker;
}
