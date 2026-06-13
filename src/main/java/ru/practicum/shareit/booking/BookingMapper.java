package ru.practicum.shareit.booking;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingResponseDto convertToDto(Booking booking);

    @Mapping(target = "item", source = "itemId", qualifiedByName = "getItemById")
    Booking convertToEntity(BookingRequestDto dto, @Context ItemService itemService);

    @Named("getItemById")
    default Item getItemById(Long itemId, @Context ItemService itemService) {
        return itemService.getItemById(itemId);
    }

}
