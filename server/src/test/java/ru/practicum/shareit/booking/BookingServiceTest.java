package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {
    private final LocalDateTime dateTime = LocalDateTime.now();

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BookingService bookingService = new BookingService(bookingRepository,
            userRepository, itemRepository, bookingMapper, itemMapper, userMapper);

    @Test
    void save_StandardBehavior() {
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(bookingService, "bookingMapper", bookingMapper);
        ReflectionTestUtils.setField(bookingService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(bookingService, "userMapper", userMapper);

        User user = createUser();
        UserDto userDto = createUserDto();
        Item item = createItem();
        ItemDto itemDto = createItemDto();
        BookingDtoWithId bookingDtoWithId = createBookingDtoWithId();
        Booking booking = createBooking();
        BookingDto bookingDto = createBookingDto();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(userMapper.usertoUserDto(any())).thenReturn(userDto);
        when(itemMapper.itemToItemDto(any(), any())).thenReturn(itemDto);
        when(bookingMapper.bookingDtotoBooking(any())).thenReturn(booking);
        when(bookingMapper.bookingtoBookingDto(any())).thenReturn(bookingDto);
        when(bookingMapper.bookingToBookingDtoWithItemAndBooker(any(),any(),any())).thenReturn(bookingDto);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto expectedBookingDto = bookingService.createBooking(bookingDtoWithId, user.getId());

        assertThat(expectedBookingDto.getId(), equalTo(bookingDtoWithId.getId()));
        assertThat(expectedBookingDto.getStart(), equalTo(bookingDtoWithId.getStart()));
        assertThat(expectedBookingDto.getEnd(), equalTo(bookingDtoWithId.getEnd()));
        assertThat(expectedBookingDto.getStatus().toString(), equalTo(bookingDtoWithId.getStatus()));
    }

    @Test
    void save_UserIdNotExist() {
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);

        Long userId = 4L;
        BookingDtoWithId bookingDtoWithId = createBookingDtoWithId();

        when(userRepository.existsById(anyLong())).thenReturn(false);

        NotFoundException notFoundException
                = assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDtoWithId, userId));
        assertThat(notFoundException.getMessage(), equalTo("Пользователь не найден в системе"));
    }

    @Test
    void save_ItemIdNotExist() {
        ReflectionTestUtils.setField(bookingService, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(bookingService, "userRepository", userRepository);
        ReflectionTestUtils.setField(bookingService, "bookingMapper", bookingMapper);

        Long userId = 4L;
        Long itemId = 4L;
        Booking booking = createBooking();
        BookingDtoWithId bookingDtoWithId = createBookingDtoWithId();
        bookingDtoWithId.toBuilder().itemId(itemId).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(false);
        when(bookingMapper.bookingDtotoBooking(any())).thenReturn(booking);

        NotFoundException notFoundException
                = assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDtoWithId, userId));
        assertThat(notFoundException.getMessage(), equalTo("Пользователь не найден в системе"));
    }

    private Booking createBooking() {
        return Booking.builder()
                .id(1L)
                .booker(createUser())
                .start(dateTime.plusDays(5))
                .end(dateTime.plusDays(10))
                .status(BookingStatus.WAITING)
                .item(createItem())
                .build();
    }

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .id(1L)
                .booker(createUserDto())
                .start(dateTime.plusDays(5))
                .end(dateTime.plusDays(10))
                .status(BookingStatus.WAITING)
                .item(createItemDto())
                .build();
    }

    private BookingDtoWithId createBookingDtoWithId() {
        return BookingDtoWithId.builder()
                .id(1L)
                .userId(createUserDto().getId())
                .start(dateTime.plusDays(5))
                .end(dateTime.plusDays(10))
                .status(BookingStatus.WAITING.toString())
                .itemId(createItemDto().getId())
                .build();
    }

    private Item createItem() {
        return Item.builder()
                .id(1L)
                .name("ноутбук")
                .description("ультратонкий")
                .available(true)
                .requestId(5L)
                .owner(createOwner())
                .build();
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("ноутбук")
                .description("ультратонкий")
                .available(true)
                .requestId(5L)
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("Kirill")
                .email("Kirill@nmicrk.ru")
                .build();
    }

    private User createOwner() {
        return User.builder()
                .id(2L)
                .name("Mariya")
                .email("Mariya@nmicrk.ru")
                .build();
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("Kirill")
                .email("Kirill@nmicrk.ru")
                .build();
    }
}
