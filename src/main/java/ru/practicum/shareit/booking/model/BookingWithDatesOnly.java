package ru.practicum.shareit.booking.model;

import java.time.LocalDateTime;

public interface BookingWithDatesOnly {
    Long getItemId();

    LocalDateTime getStart();

    LocalDateTime getEnd();
}
