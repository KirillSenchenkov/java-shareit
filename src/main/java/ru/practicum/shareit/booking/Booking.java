package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Booking {

    private Long id;

    @NotBlank
    private String name;

    private LocalDateTime start;

    private LocalDateTime end;

    private Item item;

    private User booker;

    private BookingStatus status;
}
