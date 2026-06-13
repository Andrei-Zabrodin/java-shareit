package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemWithBookingDatesDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    private LocalDateTime prevBookingStart;
    private LocalDateTime prevBookingEnd;
    private LocalDateTime nextBookingStart;
    private LocalDateTime nextBookingEnd;
}
