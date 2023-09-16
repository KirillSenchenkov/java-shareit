package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {

    public UserDto userToUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User userDtoToUser(Long id, UserDto userDto) {
        return new User(id, userDto.getName(), userDto.getEmail());
    }

    public List<UserDto> usersToUserDtoList(List<User> users) {
        return users.stream()
                .map(UserMapper::userToUserDto)
                .collect(Collectors.toList());
    }
}
