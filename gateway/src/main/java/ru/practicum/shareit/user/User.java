package ru.practicum.shareit.user;

import lombok.*;

@Data
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
}

