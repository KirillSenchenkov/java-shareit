package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadEntityException;
import ru.practicum.shareit.exception.ItemNotOwnedByUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;

    private final ItemMapper itemMapper;

    private final UserMapper userMapper;

    public BookingDto createBooking(BookingDtoWithId bookingDto, Long userId) {
        checkUserExists(userId);
        Booking booking = toBookingWithItemAndBooker(bookingDto, userId);
        checkBookingBasicConstraints(booking, userId);
        booking.setStatus(BookingStatus.WAITING);
        return toDtoWithItemAndBooker(bookingRepository.save(booking));
    }

    public BookingDto changeBookingStatus(Long bookingId, Boolean isApproved, Long requesterId) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с  id " + bookingId + " не найдено"));

        if (!Objects.equals(booking.getItem().getOwner().getId(), requesterId)) {
            throw new ItemNotOwnedByUserException("Статус бронирования может менять только владелец");
        }
        BookingStatus newStatus = isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (booking.getStatus().equals(newStatus)) {
            throw new BadEntityException("Статус бронирования уже изменен");
        }
        booking.setStatus(newStatus);
        bookingRepository.save(booking);
        return toDtoWithItemAndBooker(booking);
    }

    public BookingDto getBooking(Long requesterId, Long bookingId) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
        checkItemOwner(booking, requesterId);
        return toDtoWithItemAndBooker(booking);
    }

    public List<BookingDto> getBookingByState(Long ownerId, String state, Pageable pageable) {
        checkUserExists(ownerId);
        checkState(state);
        BookingSearchType type = BookingSearchType.valueOf(state);
        BookingSearch bookingSearch = new BookingSearch(bookingRepository);
        return bookingSearch
                .getBookings(ownerId, type, pageable)
                .stream()
                .map(this::toDtoWithItemAndBooker)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public List<BookingDto> getBookingByStateAndOwner(Long ownerId, String state, Pageable pageable) {
        checkUserExists(ownerId);
        checkState(state);
        BookingSearchType type = BookingSearchType.valueOf(state);
        BookingSearch bookingSearch = new BookingSearch(bookingRepository);

        return bookingSearch
                .getBookingsByItemsOwner(ownerId, type, pageable)
                .stream()
                .map(this::toDtoWithItemAndBooker)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    private Booking toBookingWithItemAndBooker(BookingDtoWithId bookingDto, Long userId) {
        Booking booking = bookingMapper.bookingDtotoBooking(bookingDto);
        Long itemId = bookingDto.getItemId();
        if (itemRepository.findById(itemId).isEmpty()
                || userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь или предмет не найдены в системе");
        }
        booking.setItem(itemRepository.findById(itemId).get());
        booking.setBooker(userRepository.findById(userId).get());
        return booking;
    }

    private BookingDto toDtoWithItemAndBooker(Booking booking) {
        return bookingMapper.bookingToBookingDtoWithItemAndBooker(booking,
                itemMapper.itemToItemDto(booking.getItem(), userMapper.usertoUserDto(booking.getBooker())),
                userMapper.usertoUserDto(booking.getBooker()));

    }

    private void checkItemOwner(Booking booking, Long requesterId) {
        if (!Objects.equals(booking.getBooker().getId(), requesterId)
                && !Objects.equals(booking.getItem().getOwner().getId(), requesterId)) {
            throw new ItemNotOwnedByUserException("Бронирование может быть изменено" +
                    " только владельцем предмета или автором");
        }
    }

    private void checkBookingBasicConstraints(Booking booking, Long requesterId) {
        checkUserExists(requesterId);
        Long itemId = booking.getItem().getId();
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Предмет не найден в системе");
        }
        Item item = itemRepository.findById(itemId).get();
        if (Objects.equals(item.getOwner().getId(), requesterId)) {
            throw new ItemNotOwnedByUserException("Владелец не может забронировать свой предмет");
        }
        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new BadEntityException("Нельзя забронировать недоступный предмет");
        }
        List<Booking> bookings = bookingRepository.searchByItemIdAndStartAddEnd(item.getId(),
                booking.getStart(), booking.getEnd());
        if (bookings.stream().anyMatch(b -> BookingStatus.WAITING.equals(b.getStatus())
                || BookingStatus.APPROVED.equals(b.getStatus()))) {
            throw new BadEntityException("Нельзя забронировать предмет более одного раза");
        }
    }

    private void checkUserExists(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
    }

    private void checkState(String state) {
        List<String> types = Arrays.stream(BookingSearchType.values())
                .map(Enum::toString)
                .collect(Collectors.toList());
        if (!types.contains(state)) {
            throw new BadEntityException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}