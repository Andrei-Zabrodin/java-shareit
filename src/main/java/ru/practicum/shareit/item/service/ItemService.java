package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getItems(long ownerId);

    ItemDto getItemById(long id);

    Collection<ItemDto> getItemsBySearch(String text);

    ItemDto save(ItemDto itemDto, long ownerId);

    CommentDto save(CommentDto commentDto, long userId, long itemId);

    ItemDto update(ItemDto itemDto, long ownerId, long id);

    Collection<Comment> getCommentsByItemId(long itemId);

    Collection<Comment> getCommentsByItemIds(Collection<Long> itemIds);
}
