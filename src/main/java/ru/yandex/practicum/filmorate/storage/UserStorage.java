package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    Map<Integer, User> getAllUsers();
    User getUserById(Integer id);

    User addUser(User user);

    void removeAll();
    void removeUserById(Integer id);

    User updateUser(User user);
}
