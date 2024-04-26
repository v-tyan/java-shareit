package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @InjectMocks
    ItemServiceImpl itemService;
    @Captor
    ArgumentCaptor<Item> itemArgumentCaptor;

    @Test
    void findAllTest() {
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any()))
                .thenReturn(Collections.emptyList());
        assertTrue(itemService.getItemsByUser(1L, 0, 20).isEmpty());
    }

    @Test
    void findItem_whenItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ItemNotFoundException ex = assertThrows(ItemNotFoundException.class, () -> itemService.getItem(1L, 1L));
        assertEquals("ItemNotFound", ex.getMessage());
    }

    @Test
    void create_whenItemValid_thenItemSaved() {
        User user = User.builder()
                .id(null)
                .name("user")
                .email("email@email.ru")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemDto actualItemDto = itemService.createItem(1L, itemDto);

        assertEquals(itemDto, actualItemDto);
        verify(itemRepository).save(item);
    }

    @Test
    void create_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> itemService.createItem(1L, itemDto));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void update_whenItemValid_thenItemUpdated() {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("email@email.ru")
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .request(null)
                .build();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto actual = itemService.updateItem(1L, itemDto, 1L);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item saved = itemArgumentCaptor.getValue();

        assertEquals(actual.getId(), saved.getId());
        assertEquals(actual.getName(), saved.getName());
        assertEquals(actual.getDescription(), saved.getDescription());
        assertEquals(actual.getAvailable(), saved.getAvailable());
        assertNull(actual.getRequestId());
    }

    @Test
    void update_whenItemForUpdateNotByOwner() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();

        ItemNotFoundException ex = assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(2L, itemDto, 1L));
        assertEquals("Item not found", ex.getMessage());
    }

    @Test
    void searchItem_whenTextNotBlank() {
        User user = User.builder()
                .id(1L)
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
        when(itemRepository.searchItems(anyString(),
                any())).thenReturn(Collections.singletonList(item));

        List<ItemDto> actual = itemService.searchItems("item", 0, 20);
        assertEquals(1, actual.size());
        assertEquals(ItemMapper.toItemDto(item), actual.get(0));
    }

    @Test
    void searchItem_whenTextIsBlank() {
        when(itemRepository.searchItems(anyString(),
                any())).thenReturn(Collections.emptyList());

        List<ItemDto> actual = itemService.searchItems("item", 0, 20);
        assertTrue(actual.isEmpty());
    }

    @Test
    void addComment_whenCommentValid_thenSaved() {
        User user = User.builder()
                .id(1L)
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
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                item,
                user,
                BookingStatus.APPROVED);
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Хорошая дрель")
                .authorName(user.getName())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndEndBefore(anyLong(),
                anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        Comment forSend = CommentMapper.toComment(user, item, commentDto);
        CommentDto actual = itemService.createComment(1L, commentDto, 1L);
        forSend.setCreated(actual.getCreated());

        verify(commentRepository).save(forSend);
        assertEquals(forSend.getId(), actual.getId());
        assertEquals(forSend.getText(), actual.getText());
        assertEquals(forSend.getAuthor().getName(), actual.getAuthorName());
    }
}