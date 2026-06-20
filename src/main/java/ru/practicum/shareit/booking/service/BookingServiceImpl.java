package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingWithDatesOnly;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.controller.BookingsRequestState;
import ru.practicum.shareit.exception.OwnershipConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    private Instant currentTime = Instant.now();

    @Override
    public BookingResponseDto getBooking(long userId, long bookingId) {
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

        return bookingMapper.convertToDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingsForBookerByState(long userId, BookingsRequestState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id " + userId + " не найден!"));

        List<Booking> bookings;
        switch (state) {
            case PAST -> bookings = bookingRepository.findPastByBookerId(userId, currentTime);
            case CURRENT -> bookings = bookingRepository.findCurrentByBookerId(userId, currentTime);
            case FUTURE -> bookings = bookingRepository.findFutureByBookerId(userId, currentTime);
            case WAITING -> bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
            default -> bookings = bookingRepository.findAllByBookerId(userId);
        }

        return bookings.stream()
                .map(bookingMapper::convertToDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getBookingsForOwnerByState(long userId, BookingsRequestState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id " + userId + " не найден!"));

        List<Booking> bookings;
        switch (state) {
            case PAST -> bookings = bookingRepository.findPastByItemOwnerId(userId, currentTime);
            case CURRENT -> bookings = bookingRepository.findCurrentByItemOwnerId(userId, currentTime);
            case FUTURE -> bookings = bookingRepository.findFutureByItemOwnerId(userId, currentTime);
            case WAITING -> bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED);
            default -> bookings = bookingRepository.findAllByItemOwnerId(userId);
        }

        return bookings.stream()
                .map(bookingMapper::convertToDto)
                .toList();
    }

    @Override
    public BookingResponseDto save(long userId, BookingRequestDto bookingDto) {
        Booking booking = bookingMapper.convertToEntity(bookingDto);
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new IdNotFoundException("Вещь с id " + bookingDto.getItemId() + " не найден!"));
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id " + userId + " не найден!"));

        booking.setItem(item);
        booking.setBooker(booker);
        validateBooking(booking);

        return bookingMapper.convertToDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto approve(long userId, long bookingId, boolean approved) {
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

        return bookingMapper.convertToDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingWithDatesOnly> getLastBookingsByItemIds(Set<Long> itemIds) {
        return bookingRepository.findLastBookingByItemId(itemIds, currentTime);
    }

    @Override
    public List<BookingWithDatesOnly> getNextBookingsByItemIds(Set<Long> itemIds) {
        return bookingRepository.findNextBookingByItemId(itemIds, currentTime);
    }

    @Override
    public BookingWithDatesOnly getLastBookingByItemId(long itemId) {
        return bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, currentTime);
    }

    @Override
    public BookingWithDatesOnly getNextBookingByItemId(long itemId) {
        return bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, currentTime);
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
