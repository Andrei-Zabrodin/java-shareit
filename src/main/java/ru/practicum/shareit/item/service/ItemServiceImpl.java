package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.OwnershipConflictException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<Item> getItems(long ownerId) {
        userRepository.getUserById(ownerId);

        return itemRepository.getItems(ownerId);
    }

    @Override
    public Item getItemById(long id) {
        return itemRepository.getItemById(id);
    }

    @Override
    public Collection<Item> getItemsBySearch(String text) {
        if (text.isEmpty()) {
            log.debug("Строка поиска пустая, возвращаем пустой список");
            return new ArrayList<>();
        }

        return itemRepository.getItemsBySearch(text);
    }

    @Override
    public Item save(Item item, long ownerId) {
        User owner = userRepository.getUserById(ownerId);

        item.setOwner(owner);

        return itemRepository.save(item);
    }

    @Override
    public Item update(Item item, long ownerId, long id) {
        userRepository.getUserById(ownerId);

        Item oldItem = itemRepository.getItemById(id);
        long realOwnerId = oldItem.getOwner().getId();
        if (realOwnerId != ownerId) {
            log.debug("Переданный id пользователя - {}  - не совпадает с владельца вещи - {}", ownerId, realOwnerId);
            throw new OwnershipConflictException("Вы не являетесь владельцем вещи с id" + id + "!");
        }

        item.setId(id);
        return itemRepository.update(item);
    }
}
