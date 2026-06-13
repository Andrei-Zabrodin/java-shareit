package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.Collection;

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

    private Collection<Comment> commentCollection;
}
