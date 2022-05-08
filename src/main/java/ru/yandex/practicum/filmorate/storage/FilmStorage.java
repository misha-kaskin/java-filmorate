package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    Map<Integer, Film> getAllFilms();
    Film getFilmById(Integer id);

    Film addFilm(Film film);

    void removeAll();
    void removeFilmById(Integer id);

    Film updateFilm(Film film);
}
