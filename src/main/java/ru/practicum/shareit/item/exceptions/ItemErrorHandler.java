package ru.practicum.shareit.item.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ItemErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ItemErrorResponse handleUserNotFound(final ItemNotFoundException e) {
        log.warn("Ошибка ItemNotFoundException - {}", e.getMessage());
        return new ItemErrorResponse("ItemNotFound", e.getMessage());
    }
}
