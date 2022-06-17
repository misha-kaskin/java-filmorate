package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void getAllMpaTest() {
        assertThat(mpaDbStorage.getAllMpa().size()).isEqualTo(5);
    }

    @Test
    void getMpaTest() throws NotFoundException {
        assertThat(mpaDbStorage.getMpaById(1).getId()).isEqualTo(1);
        assertThat(mpaDbStorage.getMpaById(1).getName()).isEqualTo("G");
    }
}
