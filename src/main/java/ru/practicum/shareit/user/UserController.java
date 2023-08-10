package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId ,@Valid @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public String delete(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping()
    public List<UserDto> getAllUser() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getTargetUser(@PathVariable Long userId) {
        return userService.getTargetUser(userId);
    }

}
