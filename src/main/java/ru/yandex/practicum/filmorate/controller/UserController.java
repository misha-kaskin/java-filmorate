package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public User post(@RequestBody User user) throws ValidationException {
        User newUser = userService.createNewUser(user);

        log.info("Post-запрос /users успешно выполнен");
        return newUser;
    }

    @GetMapping("/users")
    public List<User> get() {
        Map<Integer, User> users = userService.getUsers();

        log.info("Get-запрос /users успешно выполнен");
        return new ArrayList<>(users.values());
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Integer id) {
        User user = userService.getUserById(id);

        log.info("Get-запрос /users успешно выполнен");

        return user;
    }

    @PutMapping("/users")
    public User put(@RequestBody User user) throws ValidationException {
        userService.updateUser(user);

        log.info("Put-запрос /users успешно выполнен");
        return user;
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void putFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.friend(id, friendId);

        log.info("Put-запрос на добавление в друзья успешно выполнен");
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.removeFriend(id, friendId);

        log.info("Delete-запрос на удаление друга успешно выполнен");
    }

    @GetMapping("/users/{id}/friends")
    public Set<User> getUserFriends(@PathVariable Integer id) {
        Set<User> userFriends = userService.getUserFriends(id);

        log.info("Get-запрос на получение списка друзей успешно выполнен");

        return userFriends;
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        Set<User> commonFriends = userService.getCommonFriends(id, otherId);

        log.info("Get-запрос на получение списка общих друзей успешно выполнен");

        return commonFriends;
    }
}
