package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Collection<Item> getItems(long ownerId);

    Optional<Item> getItemById(long id);

    Collection<Item> getItemsBySearch(String text);

    Item postItem(Item item);

    Item patchItem(Item item, long id);

    void existsById(long id);
}
