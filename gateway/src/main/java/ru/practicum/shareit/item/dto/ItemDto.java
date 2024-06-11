package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Create;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = Create.class, message = "Item name can't be blank")
    private String name;
    @NotBlank(groups = Create.class, message = "Item description can't be blank")
    private String description;
    @NotNull(groups = Create.class, message = "Availability status is absent")
    private Boolean available;
    private Long requestId;
}