package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
