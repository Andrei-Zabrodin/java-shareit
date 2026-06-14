package ru.practicum.shareit.booking.model;

import java.time.Instant;

public interface BookingWithDatesOnly {
    Long getItemId();

    Instant getStart();

    Instant getEnd();
}
