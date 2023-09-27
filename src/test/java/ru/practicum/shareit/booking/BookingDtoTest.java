package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoTest {

    private final LocalDateTime dateTime = LocalDateTime.of(2023, 11, 18, 12, 0, 0, 0);

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;

    @Test
    void setJsonBookingDto() throws Exception {
        JsonContent<BookingDto> result = jsonBookingDto.write(createBookingDto());
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-11-13T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-11-23T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("REJECTED");

        assertThat(result).extractingJsonPathValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.booker.name").isEqualTo("Kirill");
        assertThat(result).extractingJsonPathValue("$.booker.email").isEqualTo("Kirill@nmicrk.ru");
        assertThat(result).extractingJsonPathValue("$.booker.registrationDate")
                .isEqualTo("2023-11-08T12:00:00");

        assertThat(result).extractingJsonPathValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item.name").isEqualTo("дрель");
        assertThat(result).extractingJsonPathValue("$.item.description").isEqualTo("электрическая дрель");
        assertThat(result).extractingJsonPathValue("$.item.requestId").isEqualTo(3);
        assertThat(result).extractingJsonPathValue("$.item.owner.id").isEqualTo(2);
        assertThat(result).extractingJsonPathValue("$.item.owner.name").isEqualTo("Mariya");
        assertThat(result).extractingJsonPathValue("$.item.owner.name").isEqualTo("Mariya");
        assertThat(result).extractingJsonPathValue("$.item.owner.email").isEqualTo("Mariya@nmicrk.ru");
        assertThat(result).extractingJsonPathValue("$.item.owner.registrationDate")
                .isEqualTo("2023-11-07T12:00:00");
        assertThat(result).extractingJsonPathValue("$.item.lastBooking.id").isEqualTo(11);
        assertThat(result).extractingJsonPathValue("$.item.lastBooking.bookerId").isEqualTo(22);
        assertThat(result).extractingJsonPathValue("$.item.nextBooking.id").isEqualTo(33);
        assertThat(result).extractingJsonPathValue("$.item.nextBooking.bookerId").isEqualTo(44);
        assertThat(result).extractingJsonPathValue("$.item.comments[0].id").isEqualTo(6);
        assertThat(result).extractingJsonPathValue("$.item.comments[0].authorId").isEqualTo(8);
        assertThat(result).extractingJsonPathValue("$.item.comments[0].text").isEqualTo("хорошая вещь");
        assertThat(result).extractingJsonPathValue("$.item.comments[0].authorName")
                .isEqualTo("Maikoo");
        assertThat(result).extractingJsonPathValue("$.item.comments[0].created")
                .isEqualTo("2023-11-15T12:00:00");
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("Kirill")
                .email("Kirill@nmicrk.ru")
                .registrationDate(dateTime.minusDays(10))
                .build();
    }

    private UserDto createUserDtoOwner() {
        return UserDto.builder()
                .id(2L)
                .name("Mariya")
                .email("Mariya@nmicrk.ru")
                .registrationDate(dateTime.minusDays(11))
                .build();
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("дрель")
                .description("электрическая дрель")
                .owner(createUserDtoOwner())
                .available(true)
                .requestId(3L)
                .lastBooking(new ItemBookingDto(11L, 22L))
                .nextBooking(new ItemBookingDto(33L, 44L))
                .comments(Set.of(createCommentDto()))
                .build();
    }

    private CommentDto createCommentDto() {
        return CommentDto.builder()
                .id(6L)
                .text("хорошая вещь")
                .authorId(8L)
                .authorName("Maikoo")
                .created(dateTime.minusDays(3))
                .build();
    }

    private BookingDto createBookingDto() {
        return BookingDto.builder()
                .id(1L)
                .item(createItemDto())
                .booker(createUserDto())
                .start(dateTime.minusDays(5))
                .end(dateTime.plusDays(5))
                .status(BookingStatus.REJECTED)
                .build();
    }
}