package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class UserDtoTest {

    private final LocalDateTime dateTime = LocalDateTime.of(2023, 11, 18, 12, 0, 0, 0);

    @Autowired
    private JacksonTester<UserDto> jsonUserDto;

    @Test
    void setJsonRequestDto() throws Exception {
        JsonContent<UserDto> result = jsonUserDto.write(createUserDto());
        assertThat(result)
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathStringValue("$.name").isEqualTo("Kirill");
        assertThat(result)
                .extractingJsonPathStringValue("$.email").isEqualTo("Kirill@nmicrk.ru");
        assertThat(result)
                .extractingJsonPathStringValue("$.registrationDate").isEqualTo("2023-11-08T12:00:00");
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("Kirill")
                .email("Kirill@nmicrk.ru")
                .registrationDate(dateTime.minusDays(10))
                .build();
    }
}
