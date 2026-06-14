package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;

@Getter
public class BookingRequestDto {

    @NotNull(message = "Необходимо указать дату начала бронирования вещи")
    @FutureOrPresent(message = "Дата начала бронирования должна быть не раньше текущего момента")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    private Instant start;

    @NotNull(message = "Необходимо указать дату окончания бронирования вещи")
    @FutureOrPresent(message = "Дата окончания бронирования должна быть не раньше текущего момента")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
    private Instant end;

    @NotNull(message = "Необходимо указать идентификатор вещи для бронирования")
    private Long itemId;
}
