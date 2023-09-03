package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {

    void createUser(User user);

    void updateUser(Long id, User user);

    void deleteUser(Long id);

    User getTargetUser(Long id);

    List<User> getAllUsers();

}
