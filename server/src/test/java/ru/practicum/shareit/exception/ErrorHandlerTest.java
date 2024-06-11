package ru.practicum.shareit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Objects;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.exception.BookingNotAvailableException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ErrorHandlerTest {
    private final MockMvc mvc;
    @MockBean
    UserService userService;

    @Test
    void handleBookingNotFound() throws Exception {
        when(userService.getUser(1L)).thenThrow(new BookingNotFoundException("Booking not found"));

        mvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingNotFoundException))
                .andExpect(result -> assertEquals("Booking not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void handleRequestNotFound() throws Exception {
        when(userService.getUser(1L)).thenThrow(new RequestNotFoundException("Request not found"));

        mvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RequestNotFoundException))
                .andExpect(result -> assertEquals("Request not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void handleUserAlreadyExists() throws Exception {
        when(userService.getUser(1L)).thenThrow(new UserAlreadyExistsException("User already exists"));

        mvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserAlreadyExistsException))
                .andExpect(result -> assertEquals("User already exists",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void handleBookingNotAvailable() throws Exception {
        when(userService.getUser(1L)).thenThrow(new BookingNotAvailableException("BookingNotAvailableException"));

        mvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingNotAvailableException))
                .andExpect(result -> assertEquals("BookingNotAvailableException",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void handleBadRequest() throws Exception {
        when(userService.getUser(1L)).thenThrow(new BadRequestException("BadRequestException"));

        mvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("BadRequestException",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void handleConstraintViolationException() throws Exception {
        when(userService.getUser(1L))
                .thenThrow(new ConstraintViolationException("ConstraintViolationException", new HashSet<>()));

        mvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertEquals("ConstraintViolationException",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}
