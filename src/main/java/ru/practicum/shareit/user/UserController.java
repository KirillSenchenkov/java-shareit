package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.service.Create;
import ru.practicum.shareit.service.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping()
    public UserDto createUser(@RequestBody @Validated(Create.class) UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody @Validated(Update.class) UserDto userDto) {
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
    public User getTargetUser(@PathVariable Long userId) {
        return userService.getTargetUser(userId);
    }

}
