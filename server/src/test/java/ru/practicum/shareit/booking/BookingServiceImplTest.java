package ru.practicum.shareit.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    BookingDtoRequest bookingDtoCreate = BookingDtoRequest.builder()
            .id(1L)
            .start(LocalDateTime.now().minusHours(2))
            .end(LocalDateTime.now().minusHours(1))
            .itemId(1L)
            .build();

    User user = User.builder()
            .id(1L)
            .name("user")
            .email("email@email.ru")
            .build();
    User user2 = User.builder()
            .id(2L)
            .name("user")
            .email("email@email.ru")
            .build();
    Item item = Item.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .owner(user)
            .request(null)
            .build();

    @Test
    void create_whenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemNotFoundException ex = assertThrows(ItemNotFoundException.class,
                () -> bookingService.createBooking(1L, bookingDtoCreate));
        assertEquals("Item not found", ex.getMessage());
    }

    @Test
    void create_whenUserNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> bookingService.createBooking(1L, bookingDtoCreate));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void create_whenOwnerTryingToBookHisItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> bookingService.createBooking(1L, bookingDtoCreate));
        assertEquals("Can't book your own item", ex.getMessage());
    }

    @Test
    void create_whenItemNotAvailable() {
        Item itemTest = item;
        itemTest.setId(49L);
        itemTest.setAvailable(false);
        bookingDtoCreate.setItemId(49L);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));

        BookingNotAvailableException ex = assertThrows(BookingNotAvailableException.class,
                () -> bookingService.createBooking(4L, bookingDtoCreate));
        assertEquals("Item is not available", ex.getMessage());
    }

    @Test
    void changeStatus_whenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        BookingNotFoundException ex = assertThrows(BookingNotFoundException.class,
                () -> bookingService.updateBooking(1L, 1L, true));
        assertEquals("Booking not found", ex.getMessage());
    }

    @Test
    void changeStatus_whenBooking_REJECTED() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> bookingService.updateBooking(1L, 1L, true));
        assertEquals("This booking can't changed status", ex.getMessage());
    }

    @Test
    void changeStatus_whenBooking_APPROVED() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> bookingService.updateBooking(1L, 1L, true));
        assertEquals("This booking can't changed status", ex.getMessage());
    }

    @Test
    void getBookingInfo_whenOwner_thenReturnInfo() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDtoResponse rsp = bookingService.getBooking(user.getId(), booking.getId());
        assertNotNull(rsp);
        assertEquals(booking.getItem().getName(), rsp.getItem().getName());
    }

    @Test
    void getBookingInfo_whenNotOwner() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> bookingService.getBooking(booking.getId(), 999L));
        assertEquals("This user can't access this information", ex.getMessage());
    }

    @Test
    void getBookingInfo_whenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        BookingNotFoundException ex = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBooking(1L, 1L));
        assertEquals("Booking not found", ex.getMessage());
    }

    @Test
    void getByBooker_whenBookerAllState() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerId(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        System.out.println(booking);
        List<BookingDtoResponse> resp = bookingService.getBookingFromOwner(user.getId(), "ALL", 0, 20);
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByItemOwnerId(anyLong(), any(), any());
    }

    @Test
    void getByBooker_whenBookerCurrentState() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerCurrent(anyLong(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoResponse> resp = bookingService.getBookings(user.getId(), "CURRENT", 0, 20);
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByBookerCurrent(anyLong(), any(), any(), any());
    }

    @Test
    void getByBooker_whenBookerPastState() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerPast(anyLong(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoResponse> resp = bookingService.getBookings(user.getId(), "PAST", 0, 20);
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByBookerPast(anyLong(), any(), any(), any());
    }

    @Test
    void getByBooker_whenBookerFutureState() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerFuture(anyLong(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoResponse> resp = bookingService.getBookings(user.getId(), "FUTURE", 0, 20);
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByBookerFuture(anyLong(), any(), any(), any());
    }

    @Test
    void getByBooker_whenBookerWaitingStatus() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerAndStatus(anyLong(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoResponse> resp = bookingService.getBookings(user.getId(), "WAITING", 0, 20);
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByBookerAndStatus(anyLong(), any(), any(), any());
    }

    @Test
    void getByBooker_whenBookerRejectedStatus() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerAndStatus(anyLong(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoResponse> resp = bookingService.getBookings(user.getId(), "REJECTED", 0, 20);
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByBookerAndStatus(anyLong(), any(), any(), any());
    }

    @Test
    void getByOwner_whenBookerCurrentState() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerCurrent(anyLong(), any(), any(),
                any()))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoResponse> resp = bookingService.getBookingFromOwner(user.getId(), "CURRENT", 0, 20);
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByItemOwnerCurrent(anyLong(), any(),
                any(), any());
    }

    @Test
    void getByOwner_whenBookerPastState() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerPast(anyLong(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoResponse> resp = bookingService.getBookingFromOwner(user.getId(), "PAST", 0, 20);
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByItemOwnerPast(anyLong(), any(),
                any(), any());
    }

    @Test
    void getByOwner_whenBookerFutureState() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerFuture(anyLong(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoResponse> resp = bookingService.getBookingFromOwner(user.getId(), "FUTURE", 0, 20);
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByItemOwnerFuture(anyLong(), any(),
                any(), any());
    }

    @Test
    void getByOwner_whenBookerWaitingStatus() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerAndStatus(anyLong(), any(), any(),
                any()))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoResponse> resp = bookingService.getBookingFromOwner(user.getId(), "WAITING", 0, 20);
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByItemOwnerAndStatus(anyLong(),
                any(), any(), any());
    }

    @Test
    void getByOwner_whenBookerRejectedStatus() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerAndStatus(anyLong(), any(), any(),
                any()))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoResponse> resp = bookingService.getBookingFromOwner(user.getId(), "REJECTED", 0, 20);
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingRepository, times(1)).findByItemOwnerAndStatus(anyLong(),
                any(), any(), any());
    }

    @Test
    void getByOwner_whenBookerNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> bookingService.getBookingFromOwner(user.getId(), "ALL", 0, 20));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getByOwner_whenUnsupportedStatus() {
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> bookingService.getBookingFromOwner(user.getId(), "UNKNOWNSTATE", 0, 20));
        assertEquals("Unknown state: UNKNOWNSTATE", ex.getMessage());
    }

    @Test
    void create_whenAllIsOk_thenBookingSaved() {
        Booking booking = BookingMapper.toBooking(bookingDtoCreate, item, user);
        BookingMapper.toBookingDtoForItem(booking);
        BookingDtoResponse bDto = BookingMapper.toBookingDtoResponse(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.save(any())).thenReturn(booking);
        bookingService.createBooking(2L, bookingDtoCreate);
        assertEquals(bDto.getId(), bookingDtoCreate.getId());
        verify(bookingRepository).save(any());
    }
}