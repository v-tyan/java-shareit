package ru.practicum.shareit.booking;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.validation.Create;

@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(name = "X-Sharer-User-Id") long userId,
            @Validated({ Create.class }) @RequestBody BookingDtoRequest bookingDtoRequest) {
        log.info("createBooking request userId = {}, bookingDtoRequest = {}", userId, bookingDtoRequest);
        return bookingClient.createBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(
            @PathVariable long bookingId,
            @RequestParam boolean approved,
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId) {
        log.info("updateBooking request bookingId = {}, approved = {}, ownerId = {}", bookingId, approved, ownerId);
        return bookingClient.updateBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @PathVariable long bookingId,
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId) {
        log.info("updateBooking request bookingId = {}, ownerId = {}", bookingId, ownerId);
        return bookingClient.getBooking(bookingId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("updateBooking request state = {}, ownerId = {}, from = {}, size = {}", state, ownerId, from, size);
        return bookingClient.getBookings(ownerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingFromOwner(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(name = "X-Sharer-User-Id") long ownerId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("updateBooking request state = {}, ownerId = {}, from = {}, size = {}", state, ownerId, from, size);
        return bookingClient.getBookingFromOwner(ownerId, state, from, size);
    }
}
