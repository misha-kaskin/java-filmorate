package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private String name;
    private Integer id;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
}
