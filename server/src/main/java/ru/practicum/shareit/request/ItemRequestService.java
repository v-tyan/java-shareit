package ru.practicum.shareit.request;

import java.util.List;

import ru.practicum.shareit.request.dto.ItemRequestDtoReq;
import ru.practicum.shareit.request.dto.ItemRequestDtoRsp;

public interface ItemRequestService {
    ItemRequestDtoRsp create(long userId, ItemRequestDtoReq itemRequestDtoRQ);

    List<ItemRequestDtoRsp> getAllInfo(long userId);

    ItemRequestDtoRsp getInfo(long userId, long requestId);

    List<ItemRequestDtoRsp> getRequests(long userId, int from, int size);
}
