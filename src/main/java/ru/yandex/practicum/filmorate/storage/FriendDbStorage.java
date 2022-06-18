package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Collection;

@Component
public class FriendDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_USER_FRIENDS = "SELECT friend_id " +
            "FROM friends " +
            "WHERE user_id = ?";
    private static final String SQL_ADD_TO_FRIEND = "INSERT INTO friends (user_id, friend_id, status) " +
            "VALUES (?, ?, ?)";
    private static final String SQL_UPDATE_FRIEND_STATUS = "UPDATE friends " +
            "SET status = ? " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String SQL_DELETE_FRIEND = "DELETE FROM friends " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String SQL_GET_COMMON_FRIENDS = "SELECT friend_id " +
            "FROM friends " +
            "WHERE user_id = ? AND friend_id IN(SELECT friend_id FROM friends WHERE user_id = ?)";
    private static final String SQL_CONTAIN_IN_FRIENDS = "SELECT friend_id " +
            "FROM friends " +
            "WHERE user_id = ? AND friend_id = ?";

    @Autowired
    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Integer> getUserFriends(Integer userId) {
        return jdbcTemplate.query(SQL_GET_USER_FRIENDS,
                (rs, rowNum) -> rs.getInt("friend_id"),
                userId);
    }

    public void addFriend(Integer userId, Integer friendId) throws ValidationException {
        if (!jdbcTemplate.query(SQL_CONTAIN_IN_FRIENDS,
                (rs, rowNum) -> rs.getInt("friend_id"),
                userId, friendId).isEmpty()) {
            throw new ValidationException("Пользователь уже был добавлен в друзья");
        }

        if (jdbcTemplate.query(SQL_CONTAIN_IN_FRIENDS,
                (rs, rowNum) -> rs.getInt("friend_id"),
                friendId, userId).isEmpty()) {
            jdbcTemplate.update(SQL_ADD_TO_FRIEND,
                    userId, friendId, false);
        } else {
            jdbcTemplate.update(SQL_ADD_TO_FRIEND,
                    userId, friendId, true);
            jdbcTemplate.update(SQL_UPDATE_FRIEND_STATUS,
                    true, friendId, userId);
        }
    }

    public void removeFriend(Integer userId, Integer friendId) throws NotFoundException {
        if (jdbcTemplate.query(SQL_CONTAIN_IN_FRIENDS,
                (rs, rowNum) -> rs.getInt("friend_id"),
                userId, friendId).isEmpty()) {
            throw new NotFoundException("Пользователь еще не добавлен в друзья");
        }

        if (jdbcTemplate.query(SQL_CONTAIN_IN_FRIENDS,
                (rs, rowNum) -> rs.getInt("friend_id"),
                friendId, userId).isEmpty()) {
            jdbcTemplate.update(SQL_DELETE_FRIEND,
                    userId, friendId);
        } else {
            jdbcTemplate.update(SQL_DELETE_FRIEND,
                    userId, friendId);
            jdbcTemplate.update(SQL_UPDATE_FRIEND_STATUS,
                    false, friendId, userId);
        }
    }

    public Collection<Integer> getCommonFriends(Integer userId, Integer otherId) {
        return jdbcTemplate.query(SQL_GET_COMMON_FRIENDS,
                (rs, rowNum) -> rs.getInt("friend_id"),
                userId, otherId);
    }
}
