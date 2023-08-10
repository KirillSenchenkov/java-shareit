package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage{

    private long userId = 0;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> userEmails = new HashSet<>();

    @Override
    public void createUser(User user) {
        if (userEmails.contains(user.getEmail())) {
            throw new EmailExistException("Пользователь с таким email уже присутствует в системе");
        } else {
            userId ++;
            user.setId(userId);
            users.put(userId, user);
            userEmails.add(user.getEmail());
        }

    }

    @Override
    public void updateUser(Long id, User user) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
        users.put(id, user);
        userEmails.remove(users.get(id).getEmail());
        userEmails.add(user.getEmail());
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
        userEmails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public User getTargetUser(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден в системе");
        }
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }
}
