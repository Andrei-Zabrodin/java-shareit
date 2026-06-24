package ru.practicum.shareit.booking.dto;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BookingRequestStateConverter implements Converter<String, BookingsRequestState> {

    @Override
    public BookingsRequestState convert(String str) {
        return BookingsRequestState.valueOf(str.toUpperCase());
    }
}
