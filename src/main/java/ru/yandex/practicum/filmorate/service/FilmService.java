package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
public class FilmService {
    private static final int MAX_DESCRIPTION_SIZE = 200;
    private final FilmStorage filmStorage;
    private final MpaDbStorage mpaDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, MpaDbStorage mpaDbStorage,
                       LikeDbStorage likeDbStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.likeDbStorage = likeDbStorage;
        this.userStorage = userStorage;
    }

    public Film addNewFilm(Film film) throws ValidationException, NotFoundException {
        validateFilm(film);
        if (film.getId() != null) {
            throw new ValidationException("Передан не пустой идентификатор");
        }
        return filmStorage.addFilm(film);
    }

    public Film getFilmById(Integer id) throws NotFoundException, SQLException {
        return filmStorage.getFilmById(id);
    }

    public Collection<Film> getAllFilms() throws SQLException, NotFoundException {
        return filmStorage.getAllFilms();
    }

    public Film updateFilm(Film film) throws ValidationException, NotFoundException {
        validateFilm(film);

        if (film.getId() == null) {
            throw new ValidationException("Передан пустой идентификатор");
        }
        try {
            filmStorage.getFilmById(film.getId());
        } catch (Exception e) {
            throw new NotFoundException("Не найден фильм");
        }
        return filmStorage.updateFilm(film);
    }

    public void like(Integer id, Integer userId) throws NotFoundException, ValidationException {
        try {
            userStorage.getUserById(id);
            filmStorage.getFilmById(id);
        } catch (Exception e) {
            throw new NotFoundException("Не найден фильм либо пользователь");
        }
        likeDbStorage.like(id, userId);
    }

    public void removeLike(Integer id, Integer userId) throws NotFoundException {
        try {
            userStorage.getUserById(id);
            filmStorage.getFilmById(id);
        } catch (Exception e) {
            throw new NotFoundException("Не найден фильм либо пользователь");
        }
        likeDbStorage.removeLike(id, userId);
    }

    public Collection<Film> getTenBestFilms() {
        return likeDbStorage.getPopular(10);
    }

    public Collection<Film> getBestFilm(int count) {
        return likeDbStorage.getPopular(count);
    }

    public void validateFilm(Film film) throws ValidationException, NotFoundException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Передано пустое название фильма");
            throw new ValidationException("название не может быть пустым");
        }

        if (film.getDescription() == null || film.getDescription().length() > MAX_DESCRIPTION_SIZE) {
            log.warn("Описание содержит более 200 символов");
            throw new ValidationException("Длина описания превосходит максимальную");
        }

        if (film.getDescription().isEmpty()) {
            log.warn("Передано пустое описание");
            throw new ValidationException("Передано пустое описание");
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
        if (film.getMpa() == null) {
            throw new ValidationException("Пустое возрастное ограничение");
        }
        if (film.getMpa() != null) {
            mpaDbStorage.getMpaById(film.getMpa().getId());
        }
    }
}
