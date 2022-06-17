package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LikeDbStorageTest {
    private final LikeDbStorage likeDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    @Test
    void getPopularTest() throws ValidationException, NotFoundException {
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

        Film film1 = new Film();
        film1.setId(null);
        film1.setName("film1");
        film1.setDescription("film1");
        film1.setReleaseDate(LocalDate.of(1999, 1, 1));
        film1.setDuration(100L);
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        film1.setMpa(mpa1);

        Film film2 = new Film();
        film2.setId(null);
        film2.setName("film2");
        film2.setDescription("film2");
        film2.setReleaseDate(LocalDate.of(1998, 1, 1));
        film2.setDuration(100L);
        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        film2.setMpa(mpa2);

        Film film3 = new Film();
        film3.setId(null);
        film3.setName("film3");
        film3.setDescription("film3");
        film3.setReleaseDate(LocalDate.of(1997, 1, 1));
        film3.setDuration(100L);
        Mpa mpa3 = new Mpa();
        mpa3.setId(3);
        film3.setMpa(mpa3);

        filmDbStorage.addFilm(film1);
        filmDbStorage.addFilm(film2);
        filmDbStorage.addFilm(film3);

        likeDbStorage.like(1, 1);
        likeDbStorage.like(1, 2);
        likeDbStorage.like(1, 3);
        likeDbStorage.like(2, 1);
        likeDbStorage.like(2, 2);
        likeDbStorage.like(3, 1);

        assertThat(likeDbStorage.getPopular(3).stream().findFirst().get().getId()).isEqualTo(1);

        likeDbStorage.removeLike(1, 1);
        likeDbStorage.removeLike(1, 2);

        assertThat(likeDbStorage.getPopular(3).stream().findFirst().get().getId()).isEqualTo(2);
    }
}
