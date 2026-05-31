package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Collection<Item> getItems(long ownerId);

    Item getItemById(long id);

    Collection<Item> getItemsBySearch(String text);

    Item save(Item item, long ownerId);

    Item update(Item itemDto, long ownerId, long id);
}
