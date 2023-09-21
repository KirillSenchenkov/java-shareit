package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@Validated
public class BookingController {
    private static final String BOOKING_DATE_FIELD_NAME = "start";

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    public BookingDto createBooking(@Valid @RequestBody BookingDtoWithId bookingCreateDto,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.save(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable long bookingId, @RequestParam Boolean approved,
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
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        Pageable pageable = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, BOOKING_DATE_FIELD_NAME));

        return bookingService.getBookingByState(userId, state, pageable);
    }

    @GetMapping("/owner")
    public List<BookingDto> getItemsByStateAndOwner
            (@RequestHeader("X-Sharer-User-Id") long userId,
             @RequestParam(defaultValue = "ALL") String state,
             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
             @RequestParam(defaultValue = "10") @Positive Integer size) {
        Pageable pageable = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, BOOKING_DATE_FIELD_NAME));
        return bookingService.getBookingByStateAndOwner(userId, state, pageable);
    }
}
