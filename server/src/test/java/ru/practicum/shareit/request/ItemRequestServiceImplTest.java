package ru.practicum.shareit.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoReq;
import ru.practicum.shareit.request.dto.ItemRequestDtoRsp;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemRequestRepository requestRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    User user;
    ItemRequestDtoReq itemRequestDtoReq;
    ItemRequestDtoRsp itemRequestDtoRsp;
    Item item;

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
                .requesterId(1L)
                .description("description")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .request(null)
                .build();
    }

    @Test
    void create_whenUserFound_thenSaved() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoReq, user);
        when(requestRepository.save(any())).thenReturn(itemRequest);
        ItemRequestDtoRsp actual = itemRequestService.create(user.getId(), itemRequestDtoReq);
        itemRequestDtoReq.setCreated(actual.getCreated());

        verify(requestRepository, Mockito.times(1)).save(any());
    }

    @Test
    void create_whenUserNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(new UserNotFoundException("User not found"));

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.create(1L, itemRequestDtoReq));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getRequestsInfo_whenUserFound_thenReturnRequestsList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        List<ItemRequestDtoRsp> responseList = itemRequestService.getAllInfo(user.getId());
        assertTrue(responseList.isEmpty());
        verify(requestRepository).findAllByRequestorId(anyLong());
    }

    @Test
    void getRequestsInfo_whenUserNotFound() {
        when(userRepository.findById(anyLong())).thenThrow(new UserAlreadyExistsException("User not found"));

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class, () -> itemRequestService.getAllInfo(1L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getRequestInfo_whenUserAndRequestFound_thenReturnRequestsList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoReq, user);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        item.setRequest(itemRequest);
        when(itemRepository.findByItemRequestId(anyLong())).thenReturn(Collections.singletonList(item));

        ItemRequestDtoRsp responseRequest = itemRequestService.getInfo(user.getId(), itemRequestDtoReq.getId());

        assertNotNull(responseRequest);
        verify(requestRepository).findById(anyLong());
        verify(itemRepository).findByItemRequestId(anyLong());
    }

    @Test
    void getRequestInfo_whenRequestNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyLong())).thenThrow(new RequestNotFoundException("Request not found"));

        RequestNotFoundException ex = assertThrows(RequestNotFoundException.class,
                () -> itemRequestService.getInfo(user.getId(), itemRequestDtoReq.getId()));
        assertEquals("Request not found", ex.getMessage());
    }

    @Test
    void getRequestsListTest() {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoReq, user);
        when(requestRepository.findAllPageable(anyLong(), any())).thenReturn(Collections.singletonList(itemRequest));

        List<ItemRequestDtoRsp> items = itemRequestService.getRequests(1L, 0, 20);
        assertEquals(1, items.size());
        verify(requestRepository).findAllPageable(anyLong(), any());
    }
}