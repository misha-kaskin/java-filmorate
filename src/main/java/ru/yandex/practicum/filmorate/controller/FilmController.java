package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private static final int MAX_DESCRIPTION_SIZE = 200;
    private Integer id = 1;

    @GetMapping("/films")
    public Map<Integer, Film> get() {
        log.info("Get-запрос /films успешно выполнен");
        return films;
    }

    @PostMapping("/films")
    public Film post(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Передано пустое название фильма");
            throw new ValidationException("название не может быть пустым");
        }

        if (film.getDescription() == null || film.getDescription().length() > MAX_DESCRIPTION_SIZE) {
            log.warn("Описание содержит более 200 символов");
            throw new ValidationException("Длина описания превосходит максимальную");
        }

        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата выхода фильма раньше допустимой");
            throw new ValidationException("Фильм вышел раньше самого первого фильма");
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.warn("Передана не положителная продолжительность фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        log.info("Post-запрос /films успешно выполнен");

        film.setId(id);
        id++;

        films.put(film.getId(), film);
        return film;
    }

    @PutMapping("/films")
    public Film put(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            log.info("Put-запрос /films успешно выполнен");

            films.put(film.getId(), film);
            return film;
        } else {
            log.warn("Обновление несуществующего фильма");
            throw new ValidationException("Фильм еще не загружен");
        }
    }
}
