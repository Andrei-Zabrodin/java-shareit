package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingsRequestState;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_HEADER) long userId, @PathVariable long bookingId) {
        log.info("GET /bookings/{} - Запрос бронирования по id", bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(USER_HEADER) long userId,
                                              @RequestParam(defaultValue = "ALL") BookingsRequestState state) {
        log.info("GET /bookings?state={} - Запрос бронирований пользователя с id {}", state, userId);
        return bookingClient.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForOwnerByState(@RequestHeader(USER_HEADER) long userId,
                                                             @RequestParam(defaultValue = "ALL") BookingsRequestState state) {
        log.info("GET /bookings/owner?state={} - Запрос бронирований вещей пользователя с id {}", state, userId);
        return bookingClient.getBookingsForOwner(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(USER_HEADER) long userId,
                                           @RequestBody @Valid BookingRequestDto bookingDto) {
        log.info("POST /bookings - Создание нового бронирования вещи с id {}, id создателя: {}",
                bookingDto.getItemId(), userId);
        return bookingClient.bookItem(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(USER_HEADER) long userId, @PathVariable long bookingId,
                                             @RequestParam boolean approved) {
        log.info("PATCH /bookings/{} - Изменение статуса бронирования, id пользователя: {}", bookingId, userId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }
}
