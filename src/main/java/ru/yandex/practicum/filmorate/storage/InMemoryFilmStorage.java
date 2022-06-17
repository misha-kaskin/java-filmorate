package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UploadException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private int id = 1;

    private final Map<Integer, Film> films;

    @Autowired
    public InMemoryFilmStorage(Map<Integer, Film> films) {
        this.films = films;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer id) throws NotFoundException {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundException("Фильм не найден.");
        }
    }

    @Override
    public Film addFilm(Film film) {
        if (films.containsValue(film)) {
            throw new UploadException("Фильм уже содержится.");
        } else {
            films.put(id, film);

            film.setId(id++);
            log.info("Фильм добавлен в оперативную память");

            return film;
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new UploadException("Фильм еще не загружен");
        }
    }
}
