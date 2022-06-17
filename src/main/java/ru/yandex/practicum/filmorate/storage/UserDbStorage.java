package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Primary
@Component
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_ALL_USER = "SELECT * FROM users";
    private static final String SQL_GET_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String SQL_ADD_USER = "INSERT INTO users (email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_USER = "UPDATE users " +
            "SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String SQL_GET_USER_ID = "SELECT user_id " +
            "FROM users " +
            "WHERE email = ? AND login = ? AND name = ? AND birthday = ?";

    @Autowired
    public  UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Collection<User> getAllUsers() {
        return jdbcTemplate.query(SQL_GET_ALL_USER, (rs, rowNum) -> mapUser(rs));
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setId(rs.getInt("user_id"));
        user.setLogin(rs.getString("login"));
        user.setEmail(rs.getString("email"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        return user;
    }

    @Override
    public User getUserById(Integer id) {
        return jdbcTemplate.queryForObject(SQL_GET_USER_BY_ID,
                (rs, rowNum) -> mapUser(rs),
                id);
    }

    @Override
    public User addUser(User user) throws ValidationException {
        if (!jdbcTemplate.query(SQL_GET_USER_ID,
                (rs, rowNum) -> rs.getInt("user_id"),
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday()).isEmpty()) {
            throw new ValidationException("Пользователь был добавлен");
        }
        jdbcTemplate.update(SQL_ADD_USER,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        Integer id = jdbcTemplate.queryForObject(SQL_GET_USER_ID,
                (rs, rowNum) -> rs.getInt("user_id"),
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(SQL_UPDATE_USER,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }
}
