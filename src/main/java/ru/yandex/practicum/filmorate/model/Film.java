package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;

@Data
public class Film {
    private String name;
    private Integer id;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Mpa mpa;
    private Collection<Genre> genres;
}
