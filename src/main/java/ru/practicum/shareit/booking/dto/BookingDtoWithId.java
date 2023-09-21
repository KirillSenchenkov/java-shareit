package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BookingDtoWithId {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
    private Long itemId;
    private Long userId;
}
