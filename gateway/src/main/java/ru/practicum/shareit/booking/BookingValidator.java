package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.exception.BadEntityException;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class BookingValidator {

    public void checkDto(BookItemRequestDto bookingDto) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (Objects.isNull(start)) {
            throw new BadEntityException("Время начала не может быть пустым");
        }
        if (Objects.isNull(end)) {
            throw new BadEntityException("Время окончания бронирования не может быть пустым");
        }
        if (Objects.equals(end, start)) {
            throw new BadEntityException("Время начала и окончания бронирования одинаковы");
        }
        if (end.isBefore(start)) {
            throw new BadEntityException("Окончание бронирования раньше его начала");
        }
    }

    public void checkApproved(Boolean approved) {
        if (Objects.isNull(approved)) {
            throw new BadEntityException("Поле Approved не может быть пустым");
        }
    }
}