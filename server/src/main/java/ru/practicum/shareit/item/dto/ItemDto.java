package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Collection;

@Getter
@Setter
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Instant lastBooking;
    private Instant nextBooking;
    private Collection<CommentDto> comments;
}