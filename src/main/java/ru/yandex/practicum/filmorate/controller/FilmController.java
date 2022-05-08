package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> get() {
        Map<Integer, Film> films = filmService.getAllFilms();

        log.info("Get-запрос /films успешно выполнен");
        return new ArrayList<>(films.values());
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Integer id) {
        Film film = filmService.getFilmById(id);

        log.info("Get-запрос /films успешно выполнен");
        return film;
    }

    @PostMapping("/films")
    public Film post(@RequestBody Film film) throws ValidationException {
        Film newFilm = filmService.addNewFilm(film);

        log.info("Post-запрос /films успешно выполнен");
        return newFilm;
    }

    @PutMapping("/films")
    public Film put(@RequestBody Film film) throws ValidationException {
        filmService.updateFilm(film);

        return film;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void putLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.like(id, userId);

        log.info("/put - запрос на постановку лайка выполнен успешно");
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.removeLike(id, userId);

        log.info("/delete - запрос на удаление лайка выполнен успешно");
    }

    @GetMapping("/films/popular")
    public Set<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        Set<Film> films;

        if (count == null) {
            films = filmService.getTenBestFilms();

            log.info("/get - запрос на получение 10 популярных фильмов выполнен");

        } else {
            films = filmService.getBestFilm(count);

            log.info("/get - запрос на получение "+ count +" популярных фильмов выполнен");

        }

        return films;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handlerNotFound(final NotFoundException e) {

    }
}
