package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class OfferDto {

    private final Long id;

    private final Long itemId;

    private final String itemName;

    private final Long requesterId;
}
