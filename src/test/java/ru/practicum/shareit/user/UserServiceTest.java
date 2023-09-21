package ru.practicum.shareit.user;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
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
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService = new UserService(userRepository, userMapper);

    @Test
    void create_ShouldReturnSameEntity() {
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        User user = createUser();
        UserDto userDto = createUserDto();
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.usertoUserDto(user)).thenReturn(userDto);
        when(userMapper.userDtotoUser(userDto)).thenReturn(user);

        UserDto expectedUserDto = userService.createUser(userDto);

        assertThat(expectedUserDto.getId(), equalTo(userDto.getId()));
        assertThat(expectedUserDto.getName(), equalTo(userDto.getName()));
        assertThat(expectedUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getUser_WrongIdShouldThrowException() {

        Long userId = 1L;
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);

        when(userRepository.existsById(anyLong())).thenReturn(false);

        NotFoundException notFoundException
                = assertThrows(NotFoundException.class, () -> userService.getTargetUser(userId));
        assertThat(notFoundException.getMessage(), equalTo("Пользователь не найден в системе"));
    }

    @Test
    void getUser_StandardBehavior() {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        User user = createUser();
        Optional<User> optionalUser = Optional.of(user);
        UserDto userDto = createUserDto();

        when(userRepository.findById(anyLong())).thenReturn(optionalUser);
        when(userMapper.usertoUserDto(user)).thenReturn(userDto);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        UserDto expectedUserDto = userService.getUserDto(1L);

        assertThat(expectedUserDto.getId(), equalTo(userDto.getId()));
        assertThat(expectedUserDto.getName(), equalTo(userDto.getName()));
        assertThat(expectedUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getUsers_StandardBehavior() {

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        User user = createUser();
        List<User> expectedUsers = List.of(user);
        UserDto userDto = createUserDto();
        List<UserDto> usersDto = List.of(userDto);

        when(userRepository.findAll()).thenReturn(expectedUsers);
        when(userMapper.usertoUserDto(user)).thenReturn(userDto);

        List<UserDto> expectedUserDTOs = userService.getAllUsers();

        assertThat(expectedUserDTOs.size(), equalTo(usersDto.size()));
        assertThat(expectedUserDTOs.get(0).getId(), equalTo(usersDto.get(0).getId()));
        assertThat(expectedUserDTOs.get(0).getName(), equalTo(usersDto.get(0).getName()));
        assertThat(expectedUserDTOs.get(0).getEmail(), equalTo(usersDto.get(0).getEmail()));
    }

    @Test
    void update_WrongIdShouldThrowException() {

        Long userId = 1L;
        Map<String, Object> updates = Map.of("name", "Lily", "email", "Lily@mail.ru");
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);

        when(userRepository.existsById(anyLong())).thenReturn(false);

        NotFoundException notFoundException
                = assertThrows(NotFoundException.class, () -> userService.updateUser(userId, updates));
        assertThat(notFoundException.getMessage(), equalTo("Пользователь не найден в системе"));
    }

    @Test
    void delete_WrongIdShouldThrowException() {
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        Long userId = 1L;
        when(userRepository.existsById(anyLong())).thenReturn(false);
        NotFoundException notFoundException
                = assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
        assertThat(notFoundException.getMessage(), equalTo("Пользователь не найден в системе"));
    }

    @Test
    void update_StandardBehavior_ChangeName() {

        Map<String, Object> updates = Map.of("name", "Lily");
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        when(userRepository.existsById(anyLong())).thenReturn(true);

        User user = createUser();
        User updatedUser = createUser();
        updatedUser.setName(String.valueOf(updates.get("name")));
        UserDto updatedUserDto = createUserDto();
        updatedUserDto.setName(String.valueOf(updates.get("name")));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(updatedUser);
        when(userMapper.usertoUserDto(any())).thenReturn(updatedUserDto);

        UserDto expectedUserDto = userService.updateUser(1L, updates);

        assertThat(expectedUserDto.getId(), equalTo(updatedUserDto.getId()));
        assertThat(expectedUserDto.getName(), equalTo(updatedUserDto.getName()));
        assertThat(expectedUserDto.getEmail(), equalTo(updatedUserDto.getEmail()));
    }

    @Test
    void update_StandardBehavior_ChangeEmail() {
        Map<String, Object> updates = Map.of("email", "Lily@mail.ru");
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        when(userRepository.existsById(anyLong())).thenReturn(true);

        User user = createUser();
        User updatedUser = createUser();
        updatedUser.setName(String.valueOf(updates.get("email")));
        UserDto updatedUserDto = createUserDto();
        updatedUserDto.setName(String.valueOf(updates.get("email")));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(updatedUser);
        when(userMapper.usertoUserDto(any())).thenReturn(updatedUserDto);

        UserDto expectedUserDto = userService.updateUser(1L, updates);

        assertThat(expectedUserDto.getId(), equalTo(updatedUserDto.getId()));
        assertThat(expectedUserDto.getName(), equalTo(updatedUserDto.getName()));
        assertThat(expectedUserDto.getEmail(), equalTo(updatedUserDto.getEmail()));
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("Kirill")
                .email("Kirill@nmicrk.ru")
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
