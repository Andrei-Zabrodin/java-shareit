package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Collection<Item> getItems(long ownerId);
    Item getItemById(long id);
    Collection<Item> getItemsBySearch(String text);
    Item postItem (Item item, long ownerId);
    Item patchItem (Item itemDto, long ownerId, long id);
}
