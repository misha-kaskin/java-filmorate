package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.data.relational.core.sql.In;

@Data
public class Mpa {
    private Integer id;
    private String name;
}
