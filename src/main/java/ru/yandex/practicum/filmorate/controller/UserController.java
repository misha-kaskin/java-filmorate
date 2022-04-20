package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    @PostMapping("/users")
    public User post(@RequestBody User user) {
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
            throw new ValidationException("Пользователь еще не зарегистрирован");
        }
    }
}
