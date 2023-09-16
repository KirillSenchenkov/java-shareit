package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class UserService {

    private UserRepository userRepository;

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.userDtoToUser(null, userDto);
        userRepository.save(user);
        return UserMapper.userToUserDto(user);
    }

    public User updateUser(Long id, UserDto userDto) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
        if (userRepository.findUserByEmail(userDto.getEmail()).isPresent()
                && !userRepository.findById(id).get().getEmail().equals(userDto.getEmail())) {
            throw new EmailExistException("Email присвоен другому пользователю");
        }
        User user = userRepository.findById(id).get();
        return userRepository.save(new User(
                id,
                userDto.getName() != null ? userDto.getName() : user.getName(),
                userDto.getEmail() != null ? userDto.getEmail() : user.getEmail()
        ));
    }

    public String deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
        userRepository.delete(userRepository.getById(id));
        return String.format("Пользователь с id %s удален", id);
    }


    public User getTargetUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
        return userRepository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return UserMapper.usersToUserDtoList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Boolean isExistUserById(Long userId) {
        return userRepository.existsById(userId);
    }
}
