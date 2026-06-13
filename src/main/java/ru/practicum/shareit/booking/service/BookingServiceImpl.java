package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.controller.BookingsRequestState;
import ru.practicum.shareit.exception.OwnershipConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    private LocalDateTime currentTime = LocalDateTime.now();

    @Override
    public Booking getBooking(long userId, long bookingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id " + userId + " не найден!"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IdNotFoundException("Бронирование с id " + bookingId + " не найдено!"));

        long ownerId = booking.getItem().getOwner().getId();
        long bookerId = booking.getBooker().getId();

        if (ownerId != userId && bookerId != userId) {
            throw new OwnershipConflictException("Получить данные о бронировании вещи может только её владелец "
                    + "или автор бронирования");
        }

        return booking;
    }

    @Override
    public List<Booking> getBookingsForBookerByState(long userId, BookingsRequestState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id " + userId + " не найден!"));

        List<Booking> bookings;
        switch(state) {
            case PAST -> bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, currentTime);
            case CURRENT -> bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                    currentTime, currentTime);
            case FUTURE -> bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId,
                    currentTime);
            case WAITING -> bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId,
                    BookingStatus.WAITING);
            case REJECTED -> bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId,
                    BookingStatus.REJECTED);
            default -> bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        }

        return bookings;
    }

    @Override
    public List<Booking> getBookingsForOwnerByState(long userId, BookingsRequestState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id " + userId + " не найден!"));

        List<Booking> bookings;
        switch(state) {
            case PAST -> bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId,
                    currentTime);
            case CURRENT -> bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                    currentTime, currentTime);
            case FUTURE -> bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId,
                    currentTime);
            case WAITING -> bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId,
                    BookingStatus.WAITING);
            case REJECTED -> bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId,
                    BookingStatus.REJECTED);
            default -> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
        }

        return bookings;
    }

    @Override
    public Booking save(long userId, Booking booking) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id " + userId + " не найден!"));

        validateBooking(booking);
        booking.setBooker(booker);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking approve(long userId, long bookingId, boolean approved) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Пользователь с id " + userId + " не найден!"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IdNotFoundException("Бронирование с id " + bookingId + " не найдено!"));

        long realOwnerId = booking.getItem().getOwner().getId();
        if (realOwnerId != owner.getId()) {
            log.debug("Переданный id пользователя - {}  - не совпадает с владельца вещи - {}",
                    owner.getId(), realOwnerId);
            throw new OwnershipConflictException("Вы не являетесь владельцем вещи с id"
                    + booking.getItem().getId() + "!");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return bookingRepository.save(booking);
    }

    private void validateBooking(Booking booking) {
        if (!booking.getStart().isBefore(booking.getEnd())) {
            throw new ValidationException("Дата конца бронирования должна быть после даты начала!");
        }

        if (Boolean.FALSE.equals(booking.getItem().getAvailable())) {
            throw new ValidationException("Вещь с id " + booking.getItem().getId() + " недоступна к бронированию!");
        }
    }
}
