package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.item.id=?1 " +
            "AND b.booker.id=?2 " +
            "AND b.status=?3 " +
            "AND b.end<?4 ")
    List<Booking> findBookigsToCheckForAddingAComment(
            long itemId, long bookerId, BookingStatus status, LocalDateTime time);
    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.id=?1 AND b.item.owner=?2")
    Optional<Booking> findBookingByIdForUpdate(long bookingId, long userId);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.id=?1 AND (b.item.owner=?2 OR b.booker.id=?2)")
    Optional<Booking> findBookingByBookerIdOrOwnerId(long bookingId, long userId);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.booker.id=?1 AND b.status<>'REJECTED' " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllBookingsByBookerId(long bookerId);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.status=?2 AND b.booker.id=?1 " +
            "ORDER BY b.start DESC ")
    List<Booking> findBookingsByStatusIs(long userId, BookingStatus status);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.start > ?2 AND (b.status='WAITING' OR b.status='APPROVED') AND b.booker.id=?1 " +
            "ORDER BY b.start DESC ")
    List<Booking> findFutureBookings(long userId, LocalDateTime start);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.start<?2 AND b.end>?2 AND b.booker.id=?1 " +
            "ORDER BY b.start DESC ")
    List<Booking> findCurrentBookings(long userId, LocalDateTime time);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.status='APPROVED' AND b.end<?2 AND b.booker.id=?1 " +
            "ORDER BY b.start DESC ")
    List<Booking> findPastBookings(long userId, LocalDateTime time);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.item.owner =?1 AND b.status<>'REJECTED' " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllBookingByOwnerId(long ownerId);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.status=?2 AND b.item.owner=?1 " +
            "ORDER BY b.start DESC ")
    List<Booking> findBookingsByOwnerIdAndStatus(long ownerId, BookingStatus status);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.start > ?2 AND (b.status='WAITING' OR b.status='APPROVED') AND b.item.owner=?1 " +
            "ORDER BY b.start DESC ")
    List<Booking> findFutureBookingsByOwnerId(long ownerId, LocalDateTime time);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.start<?2 AND b.end>?2 AND b.item.owner=?1 " +
            "ORDER BY b.start DESC ")
    List<Booking> findCurrentBookingsByOwnerId(long ownerId, LocalDateTime time);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.status='APPROVED' AND b.end<?2 AND b.item.owner=?1 " +
            "ORDER BY b.start DESC ")
    List<Booking> findPastBookingsByOwnerId(long ownerId, LocalDateTime time);
}
