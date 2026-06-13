package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId, LocalDateTime start,
                                                                          LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long ownerId, LocalDateTime start,
                                                                             LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

}
