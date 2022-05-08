package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private String login;
    private String name;
    private Integer id;
    private String email;
    private LocalDate birthday;
    @JsonIgnore
    private Set<Integer> friends = new HashSet<>();
}
