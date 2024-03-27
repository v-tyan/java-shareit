package ru.practicum.shareit.request;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRequest {
    private long id;

    private String description;

    private long requestor;

    private LocalDate created;
}
