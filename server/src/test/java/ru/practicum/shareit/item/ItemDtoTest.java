package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoTest {

    private final LocalDateTime dateTime = LocalDateTime.of(2023, 11, 18, 12, 0, 0, 0);

    @Autowired
    private JacksonTester<ItemDto> jsonItemDto;

    @Test
    void setJsonItemDto() throws Exception {
        JsonContent<ItemDto> result = jsonItemDto.write(createItemDto());
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("дрель");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("электрическая дрель");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);

        assertThat(result).extractingJsonPathValue("$.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.owner.name").isEqualTo("Kirill");
        assertThat(result).extractingJsonPathValue("$.owner.name").isEqualTo("Kirill");
        assertThat(result).extractingJsonPathValue("$.owner.email").isEqualTo("Kirill@nmicrk.ru");
        assertThat(result).extractingJsonPathValue("$.owner.registrationDate").isEqualTo("2023-11-08T12:00:00");

        assertThat(result).extractingJsonPathValue("$.lastBooking.id").isEqualTo(11);
        assertThat(result).extractingJsonPathValue("$.lastBooking.bookerId").isEqualTo(22);

        assertThat(result).extractingJsonPathValue("$.nextBooking.id").isEqualTo(33);
        assertThat(result).extractingJsonPathValue("$.nextBooking.bookerId").isEqualTo(44);

        assertThat(result).extractingJsonPathValue("$.comments[0].id").isEqualTo(3);
        assertThat(result).extractingJsonPathValue("$.comments[0].authorId").isEqualTo(4);
        assertThat(result).extractingJsonPathValue("$.comments[0].text").isEqualTo("Отличная вещь");
        assertThat(result).extractingJsonPathValue("$.comments[0].authorName").isEqualTo("Maikoo");
        assertThat(result).extractingJsonPathValue("$.comments[0].created").isEqualTo("2023-11-15T12:00:00");
    }

    private UserDto createUserDtoOwner() {
        return UserDto.builder()
                .id(1L)
                .name("Kirill")
                .email("Kirill@nmicrk.ru")
                .registrationDate(dateTime.minusDays(10))
                .build();
    }

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(2L)
                .name("дрель")
                .description("электрическая дрель")
                .owner(createUserDtoOwner())
                .available(true)
                .requestId(1L)
                .lastBooking(new ItemBookingDto(11L, 22L))
                .nextBooking(new ItemBookingDto(33L, 44L))
                .comments(Set.of(createCommentDto()))
                .build();
    }

    private CommentDto createCommentDto() {
        return CommentDto.builder()
                .id(3L)
                .text("Отличная вещь")
                .authorId(4L)
                .authorName("Maikoo")
                .created(dateTime.minusDays(3))
                .build();
    }
}
