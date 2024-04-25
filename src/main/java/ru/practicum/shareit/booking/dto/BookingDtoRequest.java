package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingDtoRequest {
    private long id;

    @FutureOrPresent(message = "Start date can't be in the past")
    @NotNull
    private LocalDateTime start;

    @FutureOrPresent(message = "End date can't be in the past")
    @NotNull
    private LocalDateTime end;

    @NotNull
    private long itemId;
}
