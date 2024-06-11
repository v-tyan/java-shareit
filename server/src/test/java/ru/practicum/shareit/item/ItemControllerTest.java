package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    ItemService itemService;
    @MockBean
    BookingRepository bookingRepository;

    User testUser;
    UserDto testUserDto;
    User updatedUser;
    UserDto updatedUserDto;
    Item testItem;
    ItemDto testItemDto;
    ItemDto updatedItemDto;

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
        testItem = Item.builder()
                .id(1L)
                .request(null)
                .owner(null)
                .name("name")
                .description("description")
                .available(true)
                .build();
        testItemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(null)
                .build();
        updatedItemDto = ItemDto.builder()
                .id(1L)
                .name("updated")
                .description("updated description")
                .available(true)
                .requestId(null)
                .build();
    }

    @Test
    void getAllTest() throws Exception {
        User user = UserMapper.fromUserDto(testUserDto);
        Item item = ItemMapper.fromItemDto(testItemDto, user, null);
        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);

        when(itemService.getItemsByUser(anyLong(), anyInt(), anyInt())).thenReturn(Collections.singletonList(itemDtoBooking));

        mvc.perform(get("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].description").value("description"));
    }

    @Test
    void findItemTest() throws Exception {
        User user = UserMapper.fromUserDto(testUserDto);
        Item item = ItemMapper.fromItemDto(testItemDto, user, null);
        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item);
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemDtoBooking);

        mvc.perform(get("/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    void createTest() throws Exception {
        when(itemService.createItem(1L, testItemDto)).thenReturn(testItemDto);

        mvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(testItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    void updateTest() throws Exception {
        when(itemService.updateItem(anyLong(), any(), anyLong())).thenReturn(updatedItemDto);

        mvc.perform(patch("/items/1")
                .content(objectMapper.writeValueAsString(updatedItemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("updated"))
                .andExpect(jsonPath("$.description").value("updated description"));
    }

    @Test
    void update_whenItemNotFound() throws Exception {
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenThrow(new ItemNotFoundException("Item not found"));

        mvc.perform(patch("/items/1")
                .content(objectMapper.writeValueAsString(updatedItemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ItemNotFoundException))
                .andExpect(result -> assertEquals("Item not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void searchItemTest() throws Exception {
        when(itemService.searchItems(anyString(), anyInt(), anyInt())).thenReturn(Collections.singletonList(testItemDto));

        mvc.perform(get("/items/search?text=дрель")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].description").value("description"));
    }

    @Test
    void addCommentTest() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Test")
                .authorName(testUser.getName())
                .build();

        when(itemService.createComment(anyLong(), any(), anyLong())).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test"))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));
    }
}