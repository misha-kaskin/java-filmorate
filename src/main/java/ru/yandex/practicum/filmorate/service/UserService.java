package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UploadException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createNewUser(User user) throws ValidationException {
        validateUser(user);

        return userStorage.addUser(user);
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public Map<Integer, User> getUsers() {
        return userStorage.getAllUsers();
    }

    public User updateUser(User user) throws ValidationException {
        validateUser(user);

        userStorage.updateUser(user);

        return user;
    }

    public void friend(Integer id, Integer friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        Set<Integer> userFriends = user.getFriends();
        Set<Integer> friendFriends = friend.getFriends();

        userFriends.add(friendId);
        friendFriends.add(id);
    }

    public void removeFriend(Integer id, Integer friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        Set<Integer> userFriends = user.getFriends();
        Set<Integer> friendFriends = friend.getFriends();

        if (userFriends.contains(friendId)) {
            userFriends.remove(friendId);
            friendFriends.remove(id);
        } else {
            throw new UploadException("Пользователи не состоят в друзьях.");
        }
    }

    public Set<User> getUserFriends(Integer id) {
        User user = userStorage.getUserById(id);

        Set<Integer> userFriends = user.getFriends();

        Set<User> friends = new HashSet<>();

        for (Integer friendId : userFriends) {
            friends.add(userStorage.getUserById(friendId));
        }

        return friends;
    }

    public Set<User> getCommonFriends (Integer id, Integer friendId) {
        Set<User> commonFriends = new HashSet<>();

        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        Set<Integer> userFriends = user.getFriends();
        Set<Integer> friendFriends = friend.getFriends();

        for (Integer userId : userFriends) {
            if (friendFriends.contains(userId)) {
                commonFriends.add(userStorage.getUserById(userId));
            }
        }

        return commonFriends;
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
