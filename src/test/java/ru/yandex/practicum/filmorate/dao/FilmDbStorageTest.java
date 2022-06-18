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
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;

    @Test
    void getAllFilmsTest() throws ValidationException, SQLException, NotFoundException {
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

        assertThat(filmDbStorage.getAllFilms().size()).isEqualTo(3);
    }

    @Test
    void getFilmByIdTest() throws ValidationException, NotFoundException {
        Film film1 = new Film();
        film1.setId(null);
        film1.setName("film1");
        film1.setDescription("film1");
        film1.setReleaseDate(LocalDate.of(1999, 1, 1));
        film1.setDuration(100L);
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        film1.setMpa(mpa1);

        filmDbStorage.addFilm(film1);

        assertThat(filmDbStorage.getFilmById(1).getId()).isEqualTo(1);
    }

    @Test
    void updateTest() throws ValidationException, NotFoundException {
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
        film2.setId(1);
        film2.setName("film2");
        film2.setDescription("film2");
        film2.setReleaseDate(LocalDate.of(1998, 1, 1));
        film2.setDuration(100L);
        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        film2.setMpa(mpa2);

        filmDbStorage.addFilm(film1);

        assertThat(filmDbStorage.getFilmById(1).getName()).isEqualTo("film1");

        filmDbStorage.updateFilm(film2);

        assertThat(filmDbStorage.getFilmById(1).getName()).isEqualTo("film2");
    }
}
