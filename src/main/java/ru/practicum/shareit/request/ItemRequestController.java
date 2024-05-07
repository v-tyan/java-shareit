package ru.practicum.shareit.request;

import java.util.List;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDtoReq;
import ru.practicum.shareit.request.dto.ItemRequestDtoRsp;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoRsp create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemRequestDtoReq itemRequestDtoRQ) {
        return itemRequestService.create(userId, itemRequestDtoRQ);
    }

    @GetMapping
    public List<ItemRequestDtoRsp> getAllRequestsInfo(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getAllInfo(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoRsp getRequestInfo(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId) {
        return itemRequestService.getInfo(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoRsp> getRequestsList(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return itemRequestService.getRequests(userId, from, size);
    }
}
