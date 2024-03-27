package ru.practicum.shareit.booking;

import java.time.LocalDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Booking {
    private long id;

    @FutureOrPresent(message = "Start date can't be in the past")
    private LocalDate start;

    @FutureOrPresent(message = "End date can't be in the past")
    private LocalDate end;

    @NotNull
    private long item;

    @NotNull
    private long booker;

    private BookingStatus status;
}
