package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {

    Collection<ItemRequestDto> getItemRequests(long userId);

    Collection<ItemRequestDto> getOtherItemRequests(long userId);

    ItemRequestDto getItemRequest(long userId, long requestId);

    ItemRequestDto save(long userId, ItemRequestDto dto);
}
