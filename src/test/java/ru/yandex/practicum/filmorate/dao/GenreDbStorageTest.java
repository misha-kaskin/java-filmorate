package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    void getAllGenreTest() {
        assertThat(genreDbStorage.getAllGenres().size()).isEqualTo(6);
    }

    @Test
    void getGenreTest() throws NotFoundException {
        assertThat(genreDbStorage.getGenreById(1).getId()).isEqualTo(1);
        assertThat(genreDbStorage.getGenreById(1).getName()).isEqualTo("Комедия");
    }
}
