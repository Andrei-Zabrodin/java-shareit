package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.Instant;

@Getter
@Setter
public class BookingResponseDto {
    private Long id;

    private Instant  start;
    private Instant end;

    private ItemDto item;
    private UserDto booker;
    private BookingStatus status;
}
