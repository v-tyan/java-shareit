package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    UserService userService;

    User testUser;
    UserDto testUserDto;
    User updatedUser;
    UserDto updatedUserDto;

    @BeforeEach
    void beforeEach() {
        testUser = User.builder()
                .id(1L)
                .email("email@email.com")
                .name("name")
                .build();
        testUserDto = UserDto.builder()
                .id(1L)
                .email("email@email.com")
                .name("name")
                .build();
        updatedUser = User.builder()
                .id(1L)
                .email("update@update.com")
                .name("update_name")
                .build();
        updatedUserDto = UserDto.builder()
                .id(1L)
                .email("update@update.com")
                .name("update_name")
                .build();
    }

    @Test
    void getAllTest() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(testUserDto));

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].email").value("email@email.com"));
    }

    @Test
    void getByIdTest() throws Exception {
        when(userService.getUser(1L)).thenReturn(testUserDto);

        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("email@email.com"));
    }

    @Test
    void getById_whenNotFound() throws Exception {
        when(userService.getUser(2L)).thenThrow(new UserNotFoundException("User not found"));

        mvc.perform(get("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void createTest() throws Exception {
        when(userService.createUser(testUserDto)).thenReturn(testUserDto);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("email@email.com"));
    }

    @Test
    void updateTest() throws Exception {
        when(userService.updateUser(1L, updatedUserDto)).thenReturn(updatedUserDto);

        mvc.perform(patch("/users/1").content(objectMapper.writeValueAsString(updatedUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("update_name"))
                .andExpect(jsonPath("$.email").value("update@update.com"));
    }

    @Test
    void update_whenNotFoundTest() throws Exception {
        when(userService.updateUser(1L, updatedUserDto))
                .thenThrow(new UserNotFoundException("User not found"));

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(updatedUserDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteTest() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}