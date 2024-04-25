package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {
    private long id;

    @NotBlank(message = "Item name can't be blank")
    private String name;

    @NotBlank(message = "Item description can't be blank")
    private String description;

    @NotNull(message = "Availability status is absent")
    private Boolean available;

    private Long request;
}
