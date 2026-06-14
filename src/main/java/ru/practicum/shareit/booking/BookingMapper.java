package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingResponseDto convertToDto(Booking booking);

    Booking convertToEntity(BookingRequestDto dto);

}
