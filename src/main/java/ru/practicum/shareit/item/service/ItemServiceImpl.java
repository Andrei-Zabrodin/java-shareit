package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.OwnershipConflictException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

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
}
