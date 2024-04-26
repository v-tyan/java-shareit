package ru.practicum.shareit.booking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    BookingService bookingService;

    BookingDtoResponse.Booker user = new BookingDtoResponse.Booker(
            1L,
            "name");
    BookingDtoResponse.Item item = new BookingDtoResponse.Item(
            1L,
            "name");
    BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
            .id(1L)
            .start(LocalDateTime.now().plusHours(1))
            .end(LocalDateTime.now().plusHours(2))
            .item(item)
            .booker(user)
            .status(BookingStatus.WAITING)
            .build();
    BookingDtoForItem bookingDtoForItem = BookingDtoForItem.builder()
            .id(1L)
            .start(LocalDateTime.now().plusHours(1))
            .end(LocalDateTime.now().plusHours(2))
            .bookerId(1L)
            .itemId(1L)
            .build();

    @Test
    void createTest() throws Exception {
        when(bookingService.createBooking(anyLong(), any())).thenReturn(bookingDtoResponse);

        mvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(bookingDtoForItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("name"))
                .andExpect(jsonPath("$.booker.name").value("name"));
    }

    // @Test
    // void create_isBadRequest() throws Exception {
    // when(bookingService.create(anyLong(), any())).thenReturn(bdr);
    // bookingDto.setEnd(LocalDateTime.now());
    // bookingDto.setStart(LocalDateTime.now().plusHours(1));
    //
    // mvc.perform(post("/bookings")
    // .contentType(MediaType.APPLICATION_JSON)
    // .accept(MediaType.APPLICATION_JSON)
    // .header("X-Sharer-User-Id", 1L)
    // .content(objectMapper.writeValueAsString(bookingDto)))
    // .andExpect(status().isBadRequest());
    // }

    @Test
    void changeStatusTest() throws Exception {
        bookingDtoResponse.setStatus(BookingStatus.APPROVED);
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoResponse);

        mvc.perform(patch("/bookings/1?approved=true")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getByIdTest() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDtoResponse);

        mvc.perform(get("/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("name"))
                .andExpect(jsonPath("$.booker.name").value("name"));
    }

    @Test
    void getByBookerTest() throws Exception {
        when(bookingService.getBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingDtoResponse));

        mvc.perform(get("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("name"))
                .andExpect(jsonPath("$[0].booker.name").value("name"));
    }

    @Test
    void getByOwnerTest() throws Exception {
        when(bookingService.getBookingFromOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingDtoResponse));

        mvc.perform(get("/bookings/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("name"))
                .andExpect(jsonPath("$[0].booker.name").value("name"));
    }
}