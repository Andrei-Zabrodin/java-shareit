package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.ItemShort;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface ItemRequestMapper {

    ItemRequestDto convertToDto(ItemRequest request);

    ItemRequestDto convertToDtoWithItems(ItemRequest request, Collection<ItemShort> items);

    ItemRequest convertToEntity(ItemRequestDto dto);
}
