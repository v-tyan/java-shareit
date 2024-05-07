package ru.practicum.shareit.item;

import java.util.List;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

public interface ItemService {
    public ItemDtoBooking getItem(long itemId, long userId);

    public List<ItemDtoBooking> getItemsByUser(long userId, int from, int size);

    public ItemDto createItem(long userId, ItemDto item);

    public ItemDto updateItem(long id, ItemDto item, long userId);

    public List<ItemDto> searchItems(String text, int from, int size);

    CommentDto createComment(long itemId, CommentDto commentDto, long authorId);
}
