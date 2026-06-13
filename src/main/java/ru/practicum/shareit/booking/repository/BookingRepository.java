package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u " +
            "WHERE u.id = ?1 ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(long bookerId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u " +
            "WHERE u.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findPastByBookerId(long bookerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u " +
            "WHERE u.id = ?1 AND b.start < ?2 AND b.end > ?2 ORDER BY b.start DESC")
    List<Booking> findCurrentByBookerId(long bookerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u " +
            "WHERE u.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findFutureByBookerId(long bookerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u " +
            "WHERE u.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findByBookerIdAndStatus(long bookerId, BookingStatus status);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u JOIN FETCH i.owner o " +
            "WHERE o.id = ?1 ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerId(long ownerId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u JOIN FETCH i.owner o " +
            "WHERE o.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findPastByItemOwnerId(long ownerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u JOIN FETCH i.owner o " +
            "WHERE o.id = ?1 AND b.start < ?2 AND b.end > ?2 ORDER BY b.start DESC")
    List<Booking> findCurrentByItemOwnerId(long ownerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u JOIN FETCH i.owner o " +
            "WHERE o.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findFutureByItemOwnerId(long ownerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u JOIN FETCH i.owner o " +
            "WHERE o.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdAndStatus(long ownerId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartBeforeOrderByStartDesc(long itemId, LocalDateTime currentTime);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(long itemId, LocalDateTime currentTime);
}
