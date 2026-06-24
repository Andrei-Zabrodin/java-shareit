package ru.practicum.shareit.item.model;

import org.springframework.beans.factory.annotation.Value;

public interface ItemShort {
    Long getId();

    String getName();

    @Value("#{target.owner.id}")
    Long getOwnerId();

    @Value("#{target.itemRequest.id}")
    Long getItemRequestId();
}
