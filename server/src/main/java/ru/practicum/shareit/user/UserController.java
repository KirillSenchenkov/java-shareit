package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping()
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody Map<String, Object> userUpdates, @PathVariable long userId) {
        return userService.updateUser(userId, userUpdates);
    }

    @DeleteMapping("/{userId}")
    public UserDto delete(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping()
    public List<UserDto> getAllUser() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getTargetUser(@PathVariable Long userId) {
        return userService.getUserDto(userId);
    }

}
