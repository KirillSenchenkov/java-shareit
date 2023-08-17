package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private UserStorage userStorage;

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.UserDtoToUser(null, userDto);
        userStorage.createUser(user);
        return UserMapper.userToUserDto(user);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = UserMapper.UserDtoToUser(id, userDto);
        userStorage.updateUser(id, user);
        return UserMapper.userToUserDto(user);
    }

    public String deleteUser(Long id) {
        userStorage.deleteUser(id);
        return String.format("Пользователь с id %s удален", id);
    }

    public UserDto getTargetUser(Long id) {
        return UserMapper.userToUserDto(userStorage.getTargetUser(id));
    }

    public List<UserDto> getAllUsers() {
        return UserMapper.usersToUserDtoList(userStorage.getAllUsers());
    }
}
