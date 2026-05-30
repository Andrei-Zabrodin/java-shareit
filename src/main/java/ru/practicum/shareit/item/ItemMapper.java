package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ResponseItemDto convertToDto(Item item);

    Item convertToEntity(RequestItemDto itemDto);
}
