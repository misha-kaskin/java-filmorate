package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.sql.SQLException;
import java.util.*;

@Slf4j
@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> get() throws SQLException, NotFoundException {
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Integer id) throws NotFoundException, SQLException {
        Film film = filmService.getFilmById(id);

        log.info("Get-запрос /films успешно выполнен");
        return film;
    }

    @PostMapping("/films")
    public Film post(@RequestBody Film film) throws ValidationException, NotFoundException {
        Film newFilm = filmService.addNewFilm(film);

        log.info("Post-запрос /films успешно выполнен");
        return newFilm;
    }

    @PutMapping("/films")
    public Film put(@RequestBody Film film) throws ValidationException, NotFoundException {
        filmService.updateFilm(film);

        return film;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void putLike(@PathVariable Integer id, @PathVariable Integer userId) throws NotFoundException,
            ValidationException {
        filmService.like(id, userId);

        log.info("/put - запрос на постановку лайка выполнен успешно");
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) throws NotFoundException {
        filmService.removeLike(id, userId);

        log.info("/delete - запрос на удаление лайка выполнен успешно");
    }

    @GetMapping("/films/popular")
    public Collection<Film> getPopularFilms(@RequestParam(required = false) Integer count) {
        Collection<Film> films;

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
