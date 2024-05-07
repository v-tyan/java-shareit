package ru.practicum.shareit.booking;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse createBooking(
            @RequestHeader(name = "X-Sharer-User-Id") long userId,
            @RequestBody @Valid BookingDtoRequest bookingDtoRequest) {
        return bookingService.createBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBooking(
            @PathVariable long bookingId,
            @RequestParam boolean approved,
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId) {
        return bookingService.updateBooking(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(
            @PathVariable long bookingId,
            @RequestHeader(name = "X-Sharer-User-Id") long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return bookingService.getBookings(ownerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getBookingFromOwner(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return bookingService.getBookingFromOwner(ownerId, state, from, size);
    }
}
