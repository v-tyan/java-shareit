package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.exception.BookingNotAvailableException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDtoResponse createBooking(long userId, BookingDtoRequest bookingDtoRequest) {
        log.info("Creating booking userId = {}, bookingDto = {}", userId, bookingDtoRequest);

        if (bookingDtoRequest.getStart().isEqual(bookingDtoRequest.getEnd())
                || bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new BadRequestException("Start and End not valid");
        }

        Item item = itemRepository.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Booking booking = BookingMapper.toBooking(bookingDtoRequest, item, user);

        if (Boolean.FALSE.equals(booking.getItem().getAvailable())) {
            throw new BookingNotAvailableException("Item is not available");
        }

        if (booking.getItem().getOwner().getId() == userId) {
            throw new UserNotFoundException("Can't book your own item");
        }

        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoResponse updateBooking(long bookingId, long ownerId, boolean approved) {
        log.info("Updating booking state bookingId = {}, ownerId = {}, approved = {}", bookingId, ownerId, approved);

        userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (booking.getStatus().equals(BookingStatus.APPROVED)
                || booking.getStatus().equals(BookingStatus.REJECTED)) {
            throw new BadRequestException("This booking can't changed status");
        }

        if (ownerId == booking.getItem().getOwner().getId()) {
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
        } else {
            throw new UserNotFoundException("Owner id in request and item owner don't match");
        }
    }

    @Override
    public BookingDtoResponse getBooking(long bookingId, long userId) {
        log.info("Get booking request bookingId = {}, userId = {}");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (booking.getItem().getOwner().getId() == userId || booking.getBooker().getId() == userId) {
            return BookingMapper.toBookingDtoResponse(booking);
        } else {
            throw new UserNotFoundException("This user can't access this information");
        }
    }

    @Override
    public List<BookingDtoResponse> getBookings(long ownerId, String state, int from, int size) {
        log.info("Get user bookings by state: ownerId = {}, state = {}", ownerId, state);

        checkUserAndState(ownerId, state);

        List<Booking> bookings = new ArrayList<>();

        switch (BookingStatusPresentation.valueOf(state)) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(ownerId,
                        PageRequest.of(from / size, size));
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerCurrent(
                        ownerId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"),
                        PageRequest.of(from / size, size));
                break;
            case PAST:
                bookings = bookingRepository.findByBookerPast(
                        ownerId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"),
                        PageRequest.of(from / size, size));
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerFuture(
                        ownerId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"),
                        PageRequest.of(from / size, size));
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerAndStatus(
                        ownerId,
                        BookingStatus.WAITING,
                        Sort.by(Sort.Direction.DESC, "start"),
                        PageRequest.of(from / size, size));
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerAndStatus(
                        ownerId,
                        BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.DESC, "start"),
                        PageRequest.of(from / size, size));
                break;
        }

        return BookingMapper.mapToBookingDtoResponse(bookings);
    }

    @Override
    public List<BookingDtoResponse> getBookingFromOwner(long ownerId, String state, int from, int size) {
        log.info("Get user's item bookings by state: ownerId = {}, state = {}", ownerId, state);

        checkUserAndState(ownerId, state);

        List<Booking> bookings = new ArrayList<>();

        switch (BookingStatusPresentation.valueOf(state)) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerId(
                        ownerId,
                        Sort.by(Sort.Direction.DESC, "start"),
                        PageRequest.of(from / size, size));
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerCurrent(
                        ownerId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"),
                        PageRequest.of(from / size, size));
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerPast(
                        ownerId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"),
                        PageRequest.of(from / size, size));
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerFuture(
                        ownerId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"),
                        PageRequest.of(from / size, size));
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerAndStatus(
                        ownerId,
                        BookingStatus.WAITING,
                        Sort.by(Sort.Direction.DESC, "start"),
                        PageRequest.of(from / size, size));
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerAndStatus(
                        ownerId,
                        BookingStatus.REJECTED,
                        Sort.by(Sort.Direction.DESC, "start"),
                        PageRequest.of(from / size, size));
                break;
        }

        return BookingMapper.mapToBookingDtoResponse(bookings);
    }

    private void checkUserAndState(long userId, String state) {
        BookingStatusPresentation[] states = BookingStatusPresentation.values();
        if (Arrays.stream(states).noneMatch(s -> s.name().equals(state))) {
            throw new BadRequestException("Unknown state: " + state);
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
