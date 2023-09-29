package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class ItemBookingDto {

    private final Long id;
    private final Long bookerId;
}
