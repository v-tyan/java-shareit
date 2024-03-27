package ru.practicum.shareit.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserErrorResponse {
    public String error;
    public String description;
}
