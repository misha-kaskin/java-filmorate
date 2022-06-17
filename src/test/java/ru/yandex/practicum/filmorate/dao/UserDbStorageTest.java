package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

    @Test
    void getAllUserTest() throws ValidationException {
        User user1 = new User();
        user1.setId(null);
        user1.setEmail("user1@yandex.ru");
        user1.setName("user1");
        user1.setLogin("user1");
        user1.setBirthday(LocalDate.of(1999, 1, 1));

        User user2 = new User();
        user2.setId(null);
        user2.setEmail("user2@yandex.ru");
        user2.setName("user2");
        user2.setLogin("user2");
        user2.setBirthday(LocalDate.of(1998, 1, 1));

        User user3 = new User();
        user3.setId(null);
        user3.setEmail("user3@yandex.ru");
        user3.setName("user3");
        user3.setLogin("user3");
        user3.setBirthday(LocalDate.of(1997, 1, 1));

        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        userDbStorage.addUser(user3);

        assertThat(userDbStorage.getAllUsers().size()).isEqualTo(3);
    }

    @Test
    void getUserByIdTest() throws ValidationException, NotFoundException {
        User user1 = new User();
        user1.setId(null);
        user1.setEmail("user1@yandex.ru");
        user1.setName("user1");
        user1.setLogin("user1");
        user1.setBirthday(LocalDate.of(1999, 1, 1));

        User user2 = new User();
        user2.setId(null);
        user2.setEmail("user2@yandex.ru");
        user2.setName("user2");
        user2.setLogin("user2");
        user2.setBirthday(LocalDate.of(1998, 1, 1));

        User user3 = new User();
        user3.setId(null);
        user3.setEmail("user3@yandex.ru");
        user3.setName("user3");
        user3.setLogin("user3");
        user3.setBirthday(LocalDate.of(1997, 1, 1));

        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        userDbStorage.addUser(user3);

        assertThat(userDbStorage.getUserById(1).getId()).isEqualTo(1);
        assertThat(userDbStorage.getUserById(2).getId()).isEqualTo(2);
        assertThat(userDbStorage.getUserById(3).getId()).isEqualTo(3);
    }

    @Test
    void updateUserTest() throws ValidationException, NotFoundException {
        User user1 = new User();
        user1.setId(null);
        user1.setEmail("user1@yandex.ru");
        user1.setName("user1");
        user1.setLogin("user1");
        user1.setBirthday(LocalDate.of(1999, 1, 1));

        User user2 = new User();
        user2.setId(1);
        user2.setEmail("user2@yandex.ru");
        user2.setName("user2");
        user2.setLogin("user2");
        user2.setBirthday(LocalDate.of(1998, 1, 1));

        userDbStorage.addUser(user1);

        assertThat(userDbStorage.getUserById(1).getLogin()).isEqualTo("user1");

        userDbStorage.updateUser(user2);

        assertThat(userDbStorage.getUserById(1).getLogin()).isEqualTo("user2");
    }
}
