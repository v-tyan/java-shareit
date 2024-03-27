package ru.practicum.shareit.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class UserErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public UserErrorResponse handleUserNotFound(final UserNotFoundException e) {
        log.warn("Ошибка UserNotFoundException - {}", e.getMessage());
        return new UserErrorResponse("UserNotFound", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public UserErrorResponse handleUserAlreadyExists(final UserAlreadyExistsException e) {
        log.warn("Ошибка UserAlreadyExistsException - {}", e.getMessage());
        return new UserErrorResponse("UserAlreadyExists", e.getMessage());
    }
}
