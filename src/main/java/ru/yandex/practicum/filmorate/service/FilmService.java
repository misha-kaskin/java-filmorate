package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UploadException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private static final int MAX_DESCRIPTION_SIZE = 200;

    private final FilmStorage filmStorage;

    private Set<Film> filmSet;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;

        filmSet = new LinkedHashSet<>();

        filmSet.addAll(filmStorage.getAllFilms().values());

        filmSet = filmSet.stream()
                .sorted((o1, o2) -> {
                    if (o1.getLikes().size() > o2.getLikes().size()) {
                        return -1;
                    } else {
                        return 1;
                    }
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Film addNewFilm(Film film) throws ValidationException {
        validateFilm(film);

        filmStorage.addFilm(film);
        filmSet.add(film);

        filmSet = filmSet.stream()
                .sorted((o1, o2) -> {
                    if (o1.getLikes().size() > o2.getLikes().size()) {
                        return -1;
                    } else {
                        return 1;
                    }
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return film;
    }

    public Film getFilmById(Integer id) throws NotFoundException {
        return filmStorage.getFilmById(id);
    }

    public Map<Integer, Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film updateFilm(Film film) throws ValidationException {
        validateFilm(film);

        filmStorage.updateFilm(film);

        filmSet = new LinkedHashSet<>(filmStorage.getAllFilms().values());

        filmSet = filmSet.stream()
                .sorted((o1, o2) -> {
                    if (o1.getLikes().size() > o2.getLikes().size()) {
                        return -1;
                    } else {
                        return 1;
                    }
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return film;
    }

    public void like(Integer id, Integer userId) throws NotFoundException {
        Film film = filmStorage.getFilmById(id);

        Set<Integer> likes = film.getLikes();

        likes.add(userId);

        filmSet = filmSet.stream()
                .sorted((o1, o2) -> {
                    if (o1.getLikes().size() > o2.getLikes().size()) {
                        return -1;
                    } else {
                        return 1;
                    }
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void removeLike(Integer id, Integer userId) throws NotFoundException {
        Film film = filmStorage.getFilmById(id);

        Set<Integer> likes = film.getLikes();

        if (likes.contains(userId)) {
            likes.remove(userId);

            filmSet = filmSet.stream()
                    .sorted((o1, o2) -> {
                        if (o1.getLikes().size() > o2.getLikes().size()) {
                            return -1;
                        } else {
                            return 1;
                        }
                    })
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } else {
            throw new NotFoundException("Пользователь не ставил лайк фильму.");
        }
    }

    public Set<Film> getTenBestFilms() {
        if (filmSet.size() >= 10) {
            return filmSet.stream()
                    .limit(10)
                    .collect(Collectors.toSet());
        } else {
            return filmSet;
        }
    }

    public Set<Film> getBestFilm(int count) {
        if (count > filmSet.size()) {
            throw new UploadException("Введенное число превышает общее количество фильмов.");
        } else {
            return filmSet.stream()
                    .limit(count)
                    .collect(Collectors.toSet());
        }
    }

    public void validateFilm(Film film) throws ValidationException {
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
    }
}
