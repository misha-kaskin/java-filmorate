package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_ALL_MPA = "SELECT * FROM mpa";
    private static final String SQL_GET_MPA_BY_ID = "SELECT * FROM mpa WHERE id = ?";

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Mpa> getAllMpa() {
        return jdbcTemplate.query(SQL_GET_ALL_MPA, (rs, rowNum) -> mapMpa(rs));
    }

    public Mpa getMpaById(Integer id) throws NotFoundException {
        try {
            return jdbcTemplate.queryForObject(SQL_GET_MPA_BY_ID, (rs, rowNum) -> mapMpa(rs), id);
        } catch (Exception e) {
            throw new NotFoundException("Mpa не найден");
        }
    }

    public Mpa mapMpa(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa();

        Integer id = rs.getInt("id");
        String name = rs.getString("name");

        mpa.setId(id);
        mpa.setName(name);

        return mpa;
    }
}
