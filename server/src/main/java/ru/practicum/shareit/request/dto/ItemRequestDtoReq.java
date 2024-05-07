package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRequestDtoReq {
    private long id;

    private String description;

    private long requestor;

    private LocalDateTime created;
}
