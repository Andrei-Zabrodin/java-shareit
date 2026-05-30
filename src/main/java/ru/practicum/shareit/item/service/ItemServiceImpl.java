package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.OwnershipConflictException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<Item> getItems(long ownerId) {
        userRepository.existsById(ownerId);

        return itemRepository.getItems(ownerId);
    }

    @Override
    public Item getItemById(long id) {
        return itemRepository.getItemById(id)
                .orElseThrow(() -> new IdNotFoundException("Вещи с id " + id + " нет в базе!"));
    }

    @Override
    public Collection<Item> getItemsBySearch(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.getItemsBySearch(text);
    }

    @Override
    public Item postItem(Item item, long ownerId) {
        userRepository.existsById(ownerId);
        item.setOwnerId(ownerId);

        return itemRepository.postItem(item);
    }

    @Override
    public Item patchItem(Item item, long ownerId, long id) {
        userRepository.existsById(ownerId);

        if (getItemById(id).getOwnerId() != ownerId) {
            throw new OwnershipConflictException("Вы не являетесь владельцем вещи с id" + id + "!");
        }

        return itemRepository.patchItem(item, id);
    }
}
