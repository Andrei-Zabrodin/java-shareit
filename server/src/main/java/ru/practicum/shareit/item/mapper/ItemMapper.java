package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.BookingWithDatesOnly;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemShort;

import java.util.Collection;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface ItemMapper {
    ItemDto convertToDto(Item item);

    ItemDto convertToDto(Item item, Collection<Comment> comments);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "lastBooking", source = "lastBooking.end")
    @Mapping(target = "nextBooking", source = "nextBooking.start")
    ItemDto convertToDtoWithBookingDates(Item item, BookingWithDatesOnly lastBooking, BookingWithDatesOnly nextBooking,
                                         Collection<Comment> comments);

    Item convertToEntity(ItemDto itemDto);

    ItemShortDto convertToShortDto(ItemShort item);
}
