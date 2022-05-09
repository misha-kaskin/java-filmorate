package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UploadException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id = 1;

    private final Map<Integer, Film> films;

    @Autowired
    public InMemoryFilmStorage(Map<Integer, Film> films) {
        this.films = films;
    }

    @Override
    public Map<Integer, Film> getAllFilms() {
        return films;
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
            return film;
        }
    }

    @Override
    public void removeAll() {
        if (films.isEmpty()) {
            throw  new UploadException("Список фильмов пустой.");
        } else {
            films.clear();
        }
    }

    @Override
    public void removeFilmById(Integer id) {
        if (films.containsKey(id)) {
            films.remove(id);
        } else {
            throw new UploadException("Фильм не содержится в списке.");
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
