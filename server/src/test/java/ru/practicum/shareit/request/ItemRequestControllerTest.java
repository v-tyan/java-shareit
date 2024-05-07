package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDtoReq;
import ru.practicum.shareit.request.dto.ItemRequestDtoRsp;
import ru.practicum.shareit.user.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    ItemRequestService itemRequestService;

    User user;
    ItemRequestDtoReq itemRequestDtoReq;
    ItemRequestDtoRsp itemRequestDtoRsp;

    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.com")
                .build();
        itemRequestDtoReq = ItemRequestDtoReq.builder()
                .id(1L)
                .requestor(1L)
                .description("description")
                .created(LocalDateTime.now())
                .build();
        itemRequestDtoRsp = ItemRequestDtoRsp.builder()
                .id(1L)
                .requestor(1L)
                .description("description")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void createTest() throws Exception {
        when(itemRequestService.create(anyLong(), any())).thenReturn(itemRequestDtoRsp);

        mvc.perform(post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(itemRequestDtoRsp)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requestor").value(1L))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    void getRequestsInfoTest() throws Exception {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoReq, user);
        ItemRequestDtoRsp rsp = ItemRequestMapper.toItemRequestDtoRsp(itemRequest);
        when(itemRequestService.getRequests(anyLong(), anyInt(), anyInt())).thenReturn(Collections.singletonList(rsp));

        mvc.perform(get("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestInfoTest() throws Exception {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoReq, user);
        ItemRequestDtoRsp rsp = ItemRequestMapper.toItemRequestDtoRsp(itemRequest);
        when(itemRequestService.getInfo(anyLong(), anyLong())).thenReturn(rsp);

        mvc.perform(get("/requests/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestor").value(1L))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    void getRequestsListTest() throws Exception {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoReq, user);
        ItemRequestDtoRsp rsp = ItemRequestMapper.toItemRequestDtoRsp(itemRequest);
        when(itemRequestService.getRequests(anyLong(), anyInt(), anyInt())).thenReturn(Collections.singletonList(rsp));

        mvc.perform(get("/requests/all")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestor").value(1L))
                .andExpect(jsonPath("$[0].description").value("description"));
    }
}