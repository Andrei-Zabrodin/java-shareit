package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {
    Collection<Item> getItems(long ownerId);

    Item getItemById(long id);

    Collection<Item> getItemsBySearch(String text);

    Item save(Item item);

    Item update(Item item);
}
