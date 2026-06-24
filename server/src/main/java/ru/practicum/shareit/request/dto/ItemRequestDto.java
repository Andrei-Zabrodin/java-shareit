package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.time.Instant;
import java.util.Collection;

@Getter
@Setter
public class ItemRequestDto {
    private Long id;
    private String description;
    private Instant created;
    private Collection<ItemShortDto> items;
}
