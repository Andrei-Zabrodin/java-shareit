package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.BookingWithDatesOnly;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDatesDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto convertToDto(Item item);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "prevBookingStart", source = "prevBooking.start")
    @Mapping(target = "prevBookingEnd", source = "prevBooking.end")
    @Mapping(target = "nextBookingStart", source = "nextBooking.start")
    @Mapping(target = "nextBookingEnd", source = "nextBooking.end")
    ItemWithBookingDatesDto convertToDto(Item item, BookingWithDatesOnly prevBooking, BookingWithDatesOnly nextBooking);

    Item convertToEntity(ItemDto itemDto);
}
