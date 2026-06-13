package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.controller.BookingsRequestState;

import java.util.List;

public interface BookingService {
    Booking getBooking(long userId, long bookingId);

    List<Booking> getBookingsForBookerByState(long userId, BookingsRequestState state);

    List<Booking> getBookingsForOwnerByState(long userId, BookingsRequestState state);

    Booking save(long userId, Booking booking);

    Booking approve(long userId, long bookingId, boolean approved);
}
