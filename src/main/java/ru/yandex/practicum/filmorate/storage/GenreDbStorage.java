package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_ALL_GENRES = "SELECT * FROM genres";
    private static final String SQL_GET_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Genre> getAllGenres() {
        return jdbcTemplate.query(SQL_GET_ALL_GENRES, (rs, rowNum) -> mapGenre(rs));
    }

    public Genre getGenreById(Integer id) throws NotFoundException {
        try {
            return jdbcTemplate.queryForObject(SQL_GET_GENRE_BY_ID, (rs, rowNum) -> mapGenre(rs), id);
        } catch (Exception e) {
            throw new NotFoundException("Жанр не найден");
        }
    }

    public Genre mapGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();

        Integer id = rs.getInt("genre_id");
        String name = rs.getString("name");

        genre.setId(id);
        genre.setName(name);

        return genre;
    }
}
