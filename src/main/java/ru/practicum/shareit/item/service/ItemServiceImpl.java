package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.OwnershipConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Collection<Item> getItems(long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new IdNotFoundException("Пользователя с id " + ownerId + " нет в базе!"));

        return itemRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public Item getItemById(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Вещи с id " + id + " нет в базе!"));
    }

    @Override
    public Collection<Item> getItemsBySearch(String text) {
        if (text.isEmpty()) {
            log.debug("Строка поиска пустая, возвращаем пустой список");
            return new ArrayList<>();
        }

        return itemRepository.findAllBySearch(text);
    }

    @Override
    public Item save(Item item, long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IdNotFoundException("Пользователя с id " + ownerId + " нет в базе!"));

        item.setOwner(owner);

        return itemRepository.save(item);
    }

    @Override
    public Comment save(Comment comment, long userId, long itemId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователя с id " + userId + " нет в базе!"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Вещи с id " + itemId + " нет в базе!"));

        //проверяем, что автор комментарий, действительно уже пользовался вещью
        Set<Long> bookedItemIds = bookingRepository.findPastByBookerId(userId, LocalDateTime.now()).stream()
                .map(booking -> booking.getItem().getId())
                .collect(Collectors.toSet());

        if (!bookedItemIds.contains(itemId)) {
            throw new ValidationException("Оставлять комментарии могут только пользователи, " +
                    "которые уже пользовались вещью!");
        }

        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    @Override
    public Item update(Item newItem, long ownerId, long id) {
        log.debug("На обновление переданы следующие данные: {}", newItem.toString());

        userRepository.findById(ownerId)
                .orElseThrow(() -> new IdNotFoundException("Пользователя с id " + ownerId + " нет в базе!"));

        Item oldItem = itemRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Вещи с id " + id + " нет в базе!"));

        long realOwnerId = oldItem.getOwner().getId();
        if (realOwnerId != ownerId) {
            log.debug("Переданный id пользователя - {}  - не совпадает с владельца вещи - {}", ownerId, realOwnerId);
            throw new OwnershipConflictException("Вы не являетесь владельцем вещи с id" + id + "!");
        }

        Optional.ofNullable(newItem.getName()).ifPresent(oldItem::setName);
        Optional.ofNullable(newItem.getDescription()).ifPresent(oldItem::setDescription);
        Optional.ofNullable(newItem.getAvailable()).ifPresent(oldItem::setAvailable);

        return itemRepository.save(oldItem);
    }

    @Override
    public Collection<Comment> getCommentsByItemId(long itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId);
    }

    @Override
    public Collection<Comment> getCommentsByItemIds(Collection<Long> itemIds) {
        return commentRepository.findByItemIdInOrderByCreatedDesc(itemIds);
    }
}
