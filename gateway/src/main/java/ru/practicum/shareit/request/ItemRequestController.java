package ru.practicum.shareit.request;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.request.dto.ItemRequestDtoReq;
import ru.practicum.shareit.validation.Create;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Validated(Create.class) @RequestBody ItemRequestDtoReq itemRequestDtoReq) {
        log.info("create request userId = {}, itemRequestDtoReq = {}", userId, itemRequestDtoReq);
        return itemRequestClient.create(userId, itemRequestDtoReq);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsInfo(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("getRequestsInfo request userId = {}", userId);
        return itemRequestClient.getInfo(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestInfo(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId) {
        log.info("getRequestInfo request userId = {}, requestId = {}", userId, requestId);
        return itemRequestClient.getInfo(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsList(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("getRequestsList request userId = {}, from = {}, size = {}", userId, from, size);
        return itemRequestClient.getRequestsList(userId, from, size);
    }
}
