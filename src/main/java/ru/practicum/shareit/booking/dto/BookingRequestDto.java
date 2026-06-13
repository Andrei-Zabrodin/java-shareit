package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BookingRequestDto {

    @NotNull(message = "Необходимо указать дату начала бронирования вещи")
    @FutureOrPresent(message = "Дата начала бронирования должна быть не раньше текущего момента")
    private LocalDateTime start;

    @NotNull(message = "Необходимо указать дату окончания бронирования вещи")
    @FutureOrPresent(message = "Дата окончания бронирования должна быть не раньше текущего момента")
    private LocalDateTime end;

    @NotNull(message = "Необходимо указать идентификатор вещи для бронирования")
    private Long itemId;
}
