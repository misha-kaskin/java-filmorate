package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Genre {
    private Integer id;
    private String name;

    public Genre() {
        id = null;
        name = null;
    }

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}