package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

@Data
@Builder
public class BookingDtoForItem {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;
    private long bookerId;
    private BookingStatus status;
}
