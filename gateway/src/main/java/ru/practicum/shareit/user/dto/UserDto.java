package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

@Data
@Builder
public class UserDto {
    private long id;

    @NotBlank(groups = { Create.class }, message = "Name can't be blank")
    private String name;

    @NotNull(groups = { Create.class }, message = "Invalid email")
    @Email(groups = { Update.class, Create.class }, message = "Invalid email format")
    private String email;
}