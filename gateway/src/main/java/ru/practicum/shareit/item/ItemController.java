package ru.practicum.shareit.item;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Create;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader(name = "X-Sharer-User-Id") long ownerId,
            @PathVariable long id) {
        log.info("getItem request ownerId = {}, id = {}", ownerId, id);
        return itemClient.getItem(id, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("getItemsByUser request userId = {}, from = {}, size = {}", userId, from, size);
        return itemClient.getItemsByUser(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
            @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("createItem request userId = {}, itemDto = {}", userId, itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemDto itemDto,
            @PathVariable long id) {
        log.info("updateItem request id = {}, userId = {}, itemDto = {}", id, userId, itemDto);
        return itemClient.updateItem(userId, id, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(name = "X-Sharer-User-Id") long ownerId,
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("searchItems request ownerId = {}, text = {}, from = {}, size = {}", ownerId, text, from, size);
        return itemClient.searchItems(text, ownerId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @PathVariable long itemId,
            @RequestBody @Validated(Create.class) CommentDto commentDto,
            @RequestHeader(name = "X-Sharer-User-Id") long authorId) {
        log.info("searchItems request itemId = {}, commentDto = {}, authorId = {}", itemId, commentDto, authorId);
        return itemClient.createComment(authorId, itemId, commentDto);
    }
}
