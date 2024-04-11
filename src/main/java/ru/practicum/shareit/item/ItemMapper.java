package ru.practicum.shareit.item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

public class ItemMapper {
    public static Item fromItemDto(ItemDto itemDto, User owner, ItemRequest request) {
        return Item.builder().id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(request)
                .owner(owner)
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        Long request = item.getRequest() == null ? null : item.getRequest().getId();
        return ItemDto.builder().id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(request)
                .build();
    }

    public static List<ItemDto> mapToItemDto(List<Item> users) {
        return users.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static ItemDtoBooking toItemDtoBooking(Item item) {
        return ItemDtoBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
    }
}
