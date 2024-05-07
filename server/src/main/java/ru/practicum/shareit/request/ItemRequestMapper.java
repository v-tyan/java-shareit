package ru.practicum.shareit.request;

import java.util.ArrayList;

import ru.practicum.shareit.request.dto.ItemRequestDtoReq;
import ru.practicum.shareit.request.dto.ItemRequestDtoRsp;
import ru.practicum.shareit.user.User;

public class ItemRequestMapper {
        public static ItemRequest toItemRequest(ItemRequestDtoReq itemRequestDtoReq, User requestor) {
        return ItemRequest.builder()
                .requestor(requestor)
                .description(itemRequestDtoReq.getDescription())
                .created(itemRequestDtoReq.getCreated())
                .build();
    }

    public static ItemRequestDtoRsp toItemRequestDtoRsp(ItemRequest itemRequest) {
        return ItemRequestDtoRsp.builder()
                .id(itemRequest.getId())
                .requesterId(itemRequest.getRequestor().getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(new ArrayList<>())
                .build();
    }
}
