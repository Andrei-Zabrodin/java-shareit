package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemShort;

import java.util.Collection;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByOwnerId(long ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true" +
            " AND (LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')))")
    Collection<Item> findAllBySearch(String text);

    @Query("SELECT i FROM Item AS i JOIN FETCH i.owner JOIN FETCH i.itemRequest WHERE i.itemRequest.id = ?1")
    Collection<ItemShort> findAllByItemRequestId(long requestId);

    @Query("SELECT i FROM Item AS i JOIN FETCH i.owner JOIN FETCH i.itemRequest WHERE i.itemRequest.id IN ?1")
    Collection<ItemShort> findAllByItemRequestIdIn(Set<Long> requestId);
}
