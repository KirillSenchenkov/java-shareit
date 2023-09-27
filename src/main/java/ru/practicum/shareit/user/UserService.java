package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserDto createUser(UserDto userDto) {
        return userMapper.usertoUserDto(userRepository.save(userMapper.userDtotoUser(userDto)));
    }

    public UserDto updateUser(Long id, Map<String, Object> updates) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
        User user = userRepository.findById(id).get();
        if (updates.containsKey("email")) {
            user.setEmail(String.valueOf(updates.get("email")));
        }
        if (updates.containsKey("name")) {
            user.setName(String.valueOf(updates.get("name")));
        }
        userRepository.save(user);
        return userMapper.usertoUserDto(user);
    }

    public UserDto deleteUser(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
        User deletedUser = userRepository.findById(id).get();
        userRepository.deleteById(id);
        return userMapper.usertoUserDto(deletedUser);
    }


    public User getTargetUser(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
        return userRepository.findById(id).get();
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::usertoUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserDto(Long id) {
        return userMapper.usertoUserDto(getTargetUser(id));
    }
}
