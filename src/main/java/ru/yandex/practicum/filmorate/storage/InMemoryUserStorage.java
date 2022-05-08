package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UploadException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int id = 1;

    private final Map<Integer, User> users;

    @Autowired
    public InMemoryUserStorage(Map<Integer, User> users) {
        this.users = users;
    }

    @Override
    public Map<Integer, User> getAllUsers() {
        return users;
    }

    @Override
    public User getUserById(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователь не найден.");
        }
    }

    @Override
    public User addUser(User user) {
        if (users.containsValue(user)) {
            throw new UploadException("Пользователь уже содержится.");
        } else {
            users.put(id, user);

            user.setId(id++);
            return user;
        }
    }

    @Override
    public void removeAll() {
        if (users.isEmpty()) {
            throw  new UploadException("Список пользователей пустой.");
        } else {
            users.clear();
        }
    }

    @Override
    public void removeUserById(Integer id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new UploadException("Пользователь не содержится в списке.");
        }
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new UploadException("Пользователь еще не загружен");
        }
    }
}
