package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingWithDatesOnly;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u " +
            "WHERE u.id = ?1 ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(long bookerId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u " +
            "WHERE u.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findPastByBookerId(long bookerId, Instant currentTime);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u " +
            "WHERE u.id = ?1 AND b.start < ?2 AND b.end > ?2 ORDER BY b.start DESC")
    List<Booking> findCurrentByBookerId(long bookerId, Instant currentTime);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u " +
            "WHERE u.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findFutureByBookerId(long bookerId, Instant currentTime);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u " +
            "WHERE u.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findByBookerIdAndStatus(long bookerId, BookingStatus status);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u JOIN FETCH i.owner o " +
            "WHERE o.id = ?1 ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerId(long ownerId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u JOIN FETCH i.owner o " +
            "WHERE o.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findPastByItemOwnerId(long ownerId, Instant currentTime);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u JOIN FETCH i.owner o " +
            "WHERE o.id = ?1 AND b.start < ?2 AND b.end > ?2 ORDER BY b.start DESC")
    List<Booking> findCurrentByItemOwnerId(long ownerId, Instant currentTime);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u JOIN FETCH i.owner o " +
            "WHERE o.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findFutureByItemOwnerId(long ownerId, Instant currentTime);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u JOIN FETCH i.owner o " +
            "WHERE o.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndStatus(long ownerId, BookingStatus status);

    @Query("SELECT i.id AS itemId, b.start AS start, b.end AS end FROM Booking b JOIN b.item i " +
            "WHERE i.id IN ?1 AND b.start < ?2 AND b.start = " +
            "(SELECT MAX(b2.start) FROM Booking b2 JOIN b2.item i2 WHERE i2.id = i.id AND b2.start < ?2 GROUP BY i2.id)")
    List<BookingWithDatesOnly> findNextBookingByItemId(Set<Long> itemIds, Instant currentTime);

    @Query("SELECT i.id AS itemId, b.start AS start, b.end AS end FROM Booking b JOIN b.item i " +
            "WHERE i.id IN ?1 AND b.start > ?2 AND b.start = " +
            "(SELECT MIN(b2.start) FROM Booking b2 JOIN b2.item i2 WHERE i2.id = i.id AND b2.start > ?2 GROUP BY i2.id)")
    List<BookingWithDatesOnly> findLastBookingByItemId(Set<Long> itemIds, Instant currentTime);

    BookingWithDatesOnly findFirstByItemIdAndStartAfterOrderByStartAsc(long itemId, Instant currentTime);

    BookingWithDatesOnly findFirstByItemIdAndEndBeforeOrderByEndDesc(long itemId, Instant currentTime);
}
