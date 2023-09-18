package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadEntityException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    public Booking createBooking(BookingDto bookingDto, Long ownerId) {
        Item item = itemService.getItemById(bookingDto.getItemId());
        User user = userService.getTargetUser(ownerId);

        if (!(bookingDto.getEnd().isAfter(bookingDto.getStart()))) {
            throw new BadEntityException("Конец бронирования не может быть раньше чем его начало");
        }

        Booking booking = bookingMapper.bookingDtoToBooking(bookingDto, user, item);

        if (!itemService.findUserById(ownerId)) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
        if (!item.getAvailable()) {
            throw new BadEntityException("Бронирование не доступно для предмета");
        }
        if (item.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Предмет не принадлежит пользователю");
        }
        bookingRepository.save(booking);
        return booking;
    }

    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId, Long userId) {
        if ((bookingRepository.findById(bookingId).isEmpty()) || (!itemService.findUserById(userId))) {
            throw new NotFoundException("Бронь отсутствует в системе");
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        if (!((Objects.equals(booking.getBooker().getId(), userId)) || (Objects.equals(booking.getItem().getOwnerId(), userId)))) {
            throw new NotFoundException("Предмет не принадлежит пользователю");
        }
        return booking;
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookings(Long userId, String state) {
        if (!itemService.findUserById(userId)) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
        switch (state) {
            case "ALL":
                return bookingRepository.findByBookerIdOrderByIdDesc(userId);

            case "CURRENT":
                return bookingRepository.findByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());

            case "PAST":
                return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());

            case "FUTURE":
                return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());

            case "WAITING":
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);

            case "REJECTED":
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);

            default:
                throw new UnknownStateException("Unknown state: " + state);
        }
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByOwner(Long userId, String state) {
        if (!itemService.findUserById(userId)) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
        switch (state) {
            case "ALL": {
                return bookingRepository.findByItemOwnerIdOrderByIdDesc(userId);
            }
            case "CURRENT": {
                return bookingRepository.findByItemOwnerIdAndEndAfterAndStartBeforeOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            }
            case "PAST": {
                return bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            }
            case "FUTURE": {
                return bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            }
            case "WAITING": {
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            }
            case "REJECTED": {
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            }
            default:
                throw new UnknownStateException("Unknown state: " + state);
        }
    }

    public Booking approve(Long bookingId, Long userId, Boolean bool) {
        if ((!userService.isExistUserById(userId)) || (bookingRepository.findById(bookingId).isEmpty())
                || (!Objects.equals(bookingRepository.findById(bookingId).get().getItem().getOwnerId(), userId))) {
            throw new NotFoundException("Пользователь или предмет отсутствуют в системе, " +
                    "или предмет забронирован другим пользователем");
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        if (BookingStatus.APPROVED.equals(booking.getStatus())) {
            throw new BadEntityException("Предмет уже забронирован");
        }
        if (bool) {
            booking.setStatus(BookingStatus.APPROVED);
        } else booking.setStatus(BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }
}
