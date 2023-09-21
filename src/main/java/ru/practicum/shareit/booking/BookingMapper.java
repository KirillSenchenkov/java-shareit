package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;
import ru.practicum.shareit.booking.model.Booking;


@Component
@AllArgsConstructor
public class BookingMapper {

    public Booking bookingDtotoBooking(BookingDtoWithId bookingDto) {
        BookingStatus status = bookingDto.getStatus() != null
                ? BookingStatus.valueOf(bookingDto.getStatus())
                : BookingStatus.WAITING;
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(status)
                .build();
    }

    public BookingDto bookingtoBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }
}
