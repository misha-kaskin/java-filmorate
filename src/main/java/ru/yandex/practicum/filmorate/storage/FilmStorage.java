package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.SQLException;
import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAllFilms() throws NotFoundException, SQLException;
    Film getFilmById(Integer id) throws NotFoundException, SQLException;

    Film addFilm(Film film) throws ValidationException;

    Film updateFilm(Film film) throws ValidationException;
}
