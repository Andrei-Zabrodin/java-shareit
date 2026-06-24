package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class BookingRequestDto {
    private Instant start;
    private Instant end;
    private Long itemId;
}
