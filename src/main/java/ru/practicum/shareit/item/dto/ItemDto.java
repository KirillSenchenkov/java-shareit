package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public final class ItemDto {

    private final long id;

    @NotBlank(message = "Item name absent")
    @Size(max = 255)
    private final String name;

    @NotBlank(message = "Item description absent")
    @Size(max = 255)
    private final String description;

    @NotNull(message = "Item availability absent")
    private final boolean available;

    private final Long requestId;

    private final ItemBookingDto lastBooking;

    private final ItemBookingDto nextBooking;

    private final Set<CommentDto> comments;

    private final UserDto owner;
}
