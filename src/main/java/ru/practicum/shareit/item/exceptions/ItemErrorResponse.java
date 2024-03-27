package ru.practicum.shareit.item.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemErrorResponse {
    public String error;
    public String description;
}
