package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Collection<Item> getItems(long ownerId);

    Item getItemById(long id);

    Collection<Item> getItemsBySearch(String text);

    Item save(Item item, long ownerId);

    Comment save(Comment comment, long userId, long itemId);

    Item update(Item itemDto, long ownerId, long id);

    Collection<Comment> getCommentsByItemId(long itemId);

    Collection<Comment> getCommentsByItemIds(Collection<Long> itemIds);
}
