package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Primary
@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_ALL_FILMS = "SELECT * FROM FILMS";
    private static final String SQL_GET_FILM_BY_ID = "SELECT * FROM FILMS WHERE FILM_ID = ?";
    private static final String SQL_ADD_FILM = "INSERT INTO FILMS (name, description, " +
            "release_date, duration, mpa) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_GET_FILM_GENRES = "SELECT * " +
            "FROM genres " +
            "WHERE genre_id IN(SELECT genre_id FROM film_genre WHERE film_id = ?)";
    private static final String SQL_GET_MPA = "SELECT * " +
            "FROM mpa " +
            "WHERE id = ?";
    private static final String SQL_ADD_GENRES = "INSERT INTO film_genre (film_id, genre_id)" +
            "VALUES (?, ?)";
    private static final String SQL_GET_FILM_ID = "SELECT film_id FROM films " +
            "WHERE name = ? AND description = ? " +
            "AND release_date = ? AND duration = ? AND mpa = ?";
    private static final String SQL_GET_GENRE = "SELECT * " +
            "FROM film_genre " +
            "WHERE film_id = ? AND genre_id = ?";
    private static final String SQL_UPDATE_FILM = "UPDATE films " +
            "SET film_id = ?, name = ?, description = ?, " +
            "release_date = ?, duration = ?, mpa = ? " +
            "WHERE film_id = ?";
    private static final String SQL_DELETE_GENRE = "DELETE FROM film_genre " +
            "WHERE film_id = ?";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getAllFilms() {
        List<Film> filmList = jdbcTemplate.query(SQL_GET_ALL_FILMS, (rs, rowNum) -> mapFilm(rs));

        log.debug("Запрошен список фильмов.");

        return filmList;
    }

    private Film mapFilm(ResultSet rs) throws SQLException {
        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Long duration = rs.getLong("duration");
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa"));

        String nameMpa = jdbcTemplate.queryForObject(SQL_GET_MPA,
                (rs_mpa, rowNum) -> rs_mpa.getString("name"), mpa.getId());
        mpa.setName(nameMpa);

        Collection<Genre> genres = jdbcTemplate.query(SQL_GET_FILM_GENRES,
                (rs_genre, rowNum) -> new Genre(rs_genre.getInt("genre_id"),
                        rs_genre.getString("name")),
                id);

        Film film = new Film();

        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setMpa(mpa);
        if (genres.isEmpty()) {
            film.setGenres(null);
        } else {
            film.setGenres(genres);
        }

        return film;
    }

    @Override
    public Film getFilmById(Integer id) throws NotFoundException {
        try {
            return jdbcTemplate.queryForObject(SQL_GET_FILM_BY_ID, (rs, rowNum) -> mapFilm(rs), id);
        } catch (Exception e) {
            throw new NotFoundException("Не найден фильм");
        }
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        if (!jdbcTemplate.query(SQL_GET_FILM_ID,
                (rs_id, rowNum) -> rs_id.getInt("film_id"),
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId()).isEmpty()) {
            throw new ValidationException("Фильм уже был добавлен");
        }

        jdbcTemplate.update(SQL_ADD_FILM, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());

        Integer id = jdbcTemplate.queryForObject(SQL_GET_FILM_ID,
                (rs_id, rowNum) -> rs_id.getInt("film_id"),
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());

        film.setId(id);
        Collection<Genre> genres = null;

        if (film.getGenres() != null) {
            genres = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                List<Integer> genreCollection = jdbcTemplate.query(SQL_GET_GENRE,
                        (rs, rowNum) -> rs.getInt("film_id"),
                        film.getId(), genre.getId());

                if (genreCollection.isEmpty()) {
                    jdbcTemplate.update(SQL_ADD_GENRES, film.getId(), genre.getId());
                    genres.add(genre);
                }
            }
        }

        film.setGenres(genres);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        if (jdbcTemplate.query(SQL_GET_FILM_BY_ID,
                (rs_id, rowNum) -> rs_id.getInt("film_id"),
                film.getId()).isEmpty()) {
            throw new ValidationException("Фильм не был добавлен");
        }

        jdbcTemplate.update(SQL_UPDATE_FILM, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        jdbcTemplate.update(SQL_DELETE_GENRE, film.getId());

        Collection<Genre> genres = null;

        if (film.getGenres() != null) {
            genres = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                List<Integer> genreCollection = jdbcTemplate.query(SQL_GET_GENRE,
                        (rs, rowNum) -> rs.getInt("film_id"),
                        film.getId(), genre.getId());

                if (genreCollection.isEmpty()) {
                    jdbcTemplate.update(SQL_ADD_GENRES, film.getId(), genre.getId());
                    genres.add(genre);
                }
            }
        }

        film.setGenres(genres);
        return film;
    }
}
