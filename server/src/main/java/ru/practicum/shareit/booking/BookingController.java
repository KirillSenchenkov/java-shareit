package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private static final String BOOKING_DATE_FIELD_NAME = "start";

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    public BookingDto createBooking(@RequestBody BookingDtoWithId bookingCreateDto,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.createBooking(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable long bookingId,
                             @RequestParam Boolean approved,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.changeBookingStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping("")
    public List<BookingDto> getBookingByState(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, BOOKING_DATE_FIELD_NAME));

        return bookingService.getBookingByState(userId, state, pageable);
    }

    @GetMapping("/owner")
    public List<BookingDto> getItemsByStateAndOwner(@RequestHeader("X-Sharer-User-Id") long userId,
             @RequestParam(defaultValue = "ALL") String state,
             @RequestParam(defaultValue = "0") Integer from,
             @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, BOOKING_DATE_FIELD_NAME));
        return bookingService.getBookingByStateAndOwner(userId, state, pageable);
    }
}
