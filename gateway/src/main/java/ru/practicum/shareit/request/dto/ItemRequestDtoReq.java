package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Create;

@Data
@Builder
public class ItemRequestDtoReq {
    private long id;

    @NotBlank(groups = Create.class)
    @Size(groups = Create.class, min = 1, max = 200)
    private String description;

    @NotNull
    private long requestor;

    private LocalDateTime created;
}
