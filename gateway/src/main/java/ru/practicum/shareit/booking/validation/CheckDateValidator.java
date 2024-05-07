package ru.practicum.shareit.booking.validation;

import java.time.LocalDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;

public class CheckDateValidator implements ConstraintValidator<BookingStartBeforeEndConstraint, BookingDtoRequest> {
    @Override
    public void initialize(BookingStartBeforeEndConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDtoRequest bookingDtoCreate, ConstraintValidatorContext context) {
        LocalDateTime start = bookingDtoCreate.getStart();
        LocalDateTime end = bookingDtoCreate.getEnd();
        if (start == null || end == null)
            return false;
        return start.isBefore(end);
    }
}
