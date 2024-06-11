package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.validation.BookingStartBeforeEndConstraint;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

@Data
@Builder
@BookingStartBeforeEndConstraint(groups = { Create.class, Update.class })
public class BookingDtoRequest {
    private long id;
    @FutureOrPresent(groups = Create.class, message = "Start date can't be in the past")
    private LocalDateTime start;
    @Future(groups = Create.class, message = "End date can't be in the past")
    private LocalDateTime end;
    @NotNull(groups = Create.class)
    private long itemId;
}
