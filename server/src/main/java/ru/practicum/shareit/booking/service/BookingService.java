package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.controller.BookingsRequestState;
import ru.practicum.shareit.booking.model.BookingWithDatesOnly;

import java.util.List;
import java.util.Set;

public interface BookingService {
    BookingResponseDto getBooking(long userId, long bookingId);

    List<BookingResponseDto> getBookingsForBookerByState(long userId, BookingsRequestState state);

    List<BookingResponseDto> getBookingsForOwnerByState(long userId, BookingsRequestState state);

    BookingResponseDto save(long userId, BookingRequestDto booking);

    BookingResponseDto approve(long userId, long bookingId, boolean approved);

    List<BookingWithDatesOnly> getLastBookingsByItemIds(Set<Long> itemIds);

    List<BookingWithDatesOnly> getNextBookingsByItemIds(Set<Long> itemIds);

    BookingWithDatesOnly getNextBookingByItemId(long itemId);

    BookingWithDatesOnly getLastBookingByItemId(long itemId);
}
