package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class ItemRepositoryMem implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long currentId = 0;

    @Override
    public Collection<Item> getItems(long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId()  == ownerId)
                .toList();
    }

    @Override
    public Optional<Item> getItemById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> getItemsBySearch(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .toList();
    }

    @Override
    public Item postItem(Item item) {
        item.setId(++currentId);
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item patchItem(Item item, long id) {
        log.debug("На обновление переданы следующие данные: {}", item.toString());

        Item oldItem = items.get(id);
        Optional.ofNullable(item.getName()).ifPresent(oldItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(oldItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(oldItem::setAvailable);

        return oldItem;
    }

    @Override
    public void existsById(long id) {
        log.debug("Проверка существования вещи с id: {}", id);

        if (!items.containsKey(id)) {
            log.debug("Вещь с id: {} не найдена", id);
            throw new IdNotFoundException("Вещи с id " + id + " нет в базе!");
        }
    }
}
