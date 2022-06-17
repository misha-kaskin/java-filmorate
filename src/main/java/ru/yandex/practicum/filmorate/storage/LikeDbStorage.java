package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

@Repository
public class LikeDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_REMOVE_LIKE = "DELETE FROM likes " +
            "WHERE user_id = ? AND film_id = ?";
    private static final String SQL_PUT_LIKE = "INSERT INTO likes (user_id, film_id)" +
            "VALUES (?, ?)";
    private static final String SQL_GET_LIKE = "SELECT user_id, film_id " +
            "FROM likes " +
            "WHERE user_id = ? AND film_id = ?";
    private static final String SQL_GET_MPA = "SELECT * " +
            "FROM mpa " +
            "WHERE id = ?";
    private static final String SQL_GET_FILM_GENRES = "SELECT * " +
            "FROM genres " +
            "WHERE genre_id IN(SELECT genre_id FROM film_genre WHERE film_id = ?)";
    private static final String SQL_GET_POPULAR = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa " +
            "FROM films AS f " +
            "LEFT JOIN likes AS l ON l.film_id = f.film_id " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT + ?";

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void like(Integer id, Integer userId) throws ValidationException {
        if (!jdbcTemplate.query(SQL_GET_LIKE, (rs, rowNum) -> mapLike(rs), userId, id).isEmpty()) {
            throw new ValidationException("Повторный лайк");
        } else {
            jdbcTemplate.update(SQL_PUT_LIKE, userId, id);
        }
    }

    public void removeLike(Integer id, Integer userId) throws NotFoundException {
        if (jdbcTemplate.query(SQL_GET_LIKE, (rs, rowNum) -> mapLike(rs), userId, id).isEmpty()) {
            throw new NotFoundException("Лайк пользователя не найден");
        } else {
            jdbcTemplate.update(SQL_REMOVE_LIKE, userId, id);
        }
    }

    private Like mapLike(ResultSet rs) throws SQLException {
        Like like = new Like();

        Integer userId = rs.getInt("user_id");
        Integer filmId = rs.getInt("film_id");
        like.setUserId(userId);
        like.setFilmId(filmId);

        return like;
    }

    public Collection<Film> getPopular(Integer count) {
        return jdbcTemplate.query(SQL_GET_POPULAR,
                (rs, rowNum) -> mapFilm(rs),
                count);
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
}
