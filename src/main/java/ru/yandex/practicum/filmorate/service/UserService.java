package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendDbStorage friendDbStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendDbStorage friendDbStorage) {
        this.userStorage = userStorage;
        this.friendDbStorage = friendDbStorage;
    }

    public User createNewUser(User user) throws ValidationException {
        validateUser(user);
        if (user.getId() != null) {
            throw new ValidationException("Пользователь с не пустым id");
        }
        return userStorage.addUser(user);
    }

    public User getUserById(Integer id) throws NotFoundException {
        try {
            return userStorage.getUserById(id);
        } catch (Exception e) {
            throw new NotFoundException("Не найден пользователь с id " + id);
        }
    }

    public Collection<User> getUsers() {
        return userStorage.getAllUsers();
    }

    public User updateUser(User user) throws ValidationException, NotFoundException {
        validateUser(user);
        if (user.getId() == null) {
            throw new ValidationException("Пользователь с пустым id");
        }
        try {
            userStorage.getUserById(user.getId());
        } catch (Exception e) {
            throw new NotFoundException("Не найден пользователь с id " + user.getId());
        }
        userStorage.updateUser(user);
        return user;
    }

    public void friend(Integer id, Integer friendId) throws NotFoundException, ValidationException {
        try {
            userStorage.getUserById(id);
            userStorage.getUserById(friendId);
        } catch (Exception e) {
            throw new NotFoundException("Не найден пользователь с id " + id);
        }
        friendDbStorage.addFriend(id, friendId);
    }

    public void removeFriend(Integer id, Integer friendId) throws NotFoundException {
        try {
            userStorage.getUserById(id);
            userStorage.getUserById(friendId);
        } catch (Exception e) {
            throw new NotFoundException("Не найден пользователь с id " + id);
        }
        friendDbStorage.removeFriend(id, friendId);
    }

    public Collection<User> getUserFriends(Integer id) throws NotFoundException {
        try {
            userStorage.getUserById(id);
        } catch (Exception e) {
            throw new NotFoundException("Не найден пользователь с id " + id);
        }

        Collection<Integer> friendsId = friendDbStorage.getUserFriends(id);
        Collection<User> friends = new ArrayList<>();
        for (Integer friendId : friendsId) {
            friends.add(userStorage.getUserById(friendId));
        }
        return friends;
    }

    public Collection<User> getCommonFriends (Integer id, Integer friendId) throws NotFoundException {
        try {
            userStorage.getUserById(id);
            userStorage.getUserById(friendId);
        } catch (Exception e) {
            throw new NotFoundException("Не найден пользователь с id " + id);
        }

        Collection<Integer> friendsId = friendDbStorage.getCommonFriends(id, friendId);
        Collection<User> friends = new ArrayList<>();
        for (Integer friend : friendsId) {
            friends.add(userStorage.getUserById(friend));
        }
        return friends;
    }

    public void validateUser(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Передана пустая почта");
            throw new ValidationException("Электронная почта не может быть пустой");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("Почта не содержит - @");
            throw new ValidationException("Почта должна содержать - @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Передан пустой логин");
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("Логин содержит пробелы");
            throw new ValidationException("Логин не может содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Передана дата рождения из будущего");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}