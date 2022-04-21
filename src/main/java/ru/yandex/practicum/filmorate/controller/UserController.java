package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UploadException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
public class UserController {
    private Map<Integer, User> users = new ConcurrentHashMap<>();
    private Integer id = 1;

    @PostMapping("/users")
    public User post(@RequestBody User user) throws ValidationException {
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

        log.info("Post-запрос /users успешно выполнен");

        user.setId(id);
        id++;

        users.put(user.getId(), user);
        return user;
    }

    @GetMapping("/users")
    public Map<Integer, User> get() {
        log.info("Get-запрос /users успешно выполнен");
        return users;
    }

    @PutMapping("/users")
    public User put(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            log.info("Put-запрос /users успешно выполнен");
            users.put(user.getId(), user);
            return user;
        } else {
            log.warn("Обновление несуществующего пользователя");
            throw new UploadException("Пользователь еще не зарегистрирован");
        }
    }
}
