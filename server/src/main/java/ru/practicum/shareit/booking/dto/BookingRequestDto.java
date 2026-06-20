package ru.practicum.shareit.booking.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class BookingRequestDto {
    private Instant start;
    private Instant end;
    private Long itemId;
}
