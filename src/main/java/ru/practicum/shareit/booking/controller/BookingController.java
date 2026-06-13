package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final static String USER_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader(USER_HEADER) long userId, @PathVariable long bookingId) {
        log.info("GET /bookings/{} - Запрос бронирования по id", bookingId);

        return bookingMapper.convertToDto(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsForBookerByState(@RequestHeader(USER_HEADER) long userId,
                                                        @RequestParam(defaultValue = "ALL") BookingsRequestState state) {
        log.info("GET /bookings?state={} - Запрос бронирований пользователя с id {}", state, userId);

        return bookingService.getBookingsForBookerByState(userId, state).stream()
                .map(bookingMapper::convertToDto)
                .toList();
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsForOwnerByState(@RequestHeader(USER_HEADER) long userId,
                                                       @RequestParam(defaultValue = "ALL") BookingsRequestState state) {
        log.info("GET /bookings/owner?state={} - Запрос бронирований вещей пользователя с id {}", state, userId);

        return bookingService.getBookingsForOwnerByState(userId, state).stream()
                .map(bookingMapper::convertToDto)
                .toList();
    }


    @PostMapping
    public BookingResponseDto postBooking(@RequestHeader(USER_HEADER) long userId,
                                  @Valid @RequestBody BookingRequestDto bookingDto) {
        log.info("POST /bookings - Создание нового бронирования вещи с id {}, id создателя: {}",
                bookingDto.getItemId(), userId);

        Booking booking = bookingMapper.convertToEntity(bookingDto, itemService);

        return bookingMapper.convertToDto(bookingService.save(userId, booking));
    }


    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader(USER_HEADER) long userId, @PathVariable long bookingId,
                                     @RequestParam boolean approved) {
        log.info("PATCH /bookings/{} - Изменение статуса бронирования, id пользователя: {}", bookingId, userId);

        return bookingMapper.convertToDto(bookingService.approve(userId, bookingId, approved));
    }
}
