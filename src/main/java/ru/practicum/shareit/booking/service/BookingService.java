package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.controller.BookingsRequestState;
import ru.practicum.shareit.booking.model.BookingWithDatesOnly;

import java.util.List;
import java.util.Set;

public interface BookingService {
    Booking getBooking(long userId, long bookingId);

    List<Booking> getBookingsForBookerByState(long userId, BookingsRequestState state);

    List<Booking> getBookingsForOwnerByState(long userId, BookingsRequestState state);

    Booking save(long userId, Booking booking);

    Booking approve(long userId, long bookingId, boolean approved);

    List<BookingWithDatesOnly> getPrevBookingsByItemIds(Set<Long> itemIds);

    List<BookingWithDatesOnly> getNextBookingsByItemIds(Set<Long> itemIds);
}
