package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemAndBookerAndStatusAndEndLessThan(Item item,
                                                                User booker,
                                                                BookingStatus status,
                                                                LocalDateTime time);

    Optional<Booking> findBookingByIdAndItem_Owner_idIs(long id, long itemOwnerId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE b.id=?1 AND (b.item.owner.id=?2 OR b.booker.id=?2)")
    Optional<Booking> findBookingByBookerIdOrOwnerId(long bookingId, long userId);

    List<Booking> findBookingsByBooker_id(long bookerId, Pageable pageable);

    List<Booking> findBookingsByStatusIsAndBooker_Id(BookingStatus status, long userId, Pageable pageable);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.start > ?2 AND (b.status='WAITING' OR b.status='APPROVED') AND b.booker.id=?1 ")
    List<Booking> findFutureBookings(long userId, LocalDateTime start, Pageable pageable);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE ?2 BETWEEN b.start AND b.end AND b.booker.id=?1 ")
    List<Booking> findCurrentBookings(long userId, LocalDateTime time, Pageable pageable);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.status='APPROVED' AND b.end<?2 AND b.booker.id=?1 ")
    List<Booking> findPastBookings(long userId, LocalDateTime time, Pageable pageable);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.item.owner.id =?1 ")
    List<Booking> findAllBookingByOwnerId(long ownerId, Pageable pageable);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.status=?2 AND b.item.owner.id=?1 ")
    List<Booking> findBookingsByOwnerIdAndStatus(long ownerId, BookingStatus status, Pageable pageable);

    List<Booking> findBookingsByStatusIsAndItem_Owner_Id(BookingStatus status, long ownerId,Pageable pageable);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.start > ?2 AND (b.status='WAITING' OR b.status='APPROVED') AND b.item.owner.id=?1 ")
    List<Booking> findFutureBookingsByOwnerId(long ownerId, LocalDateTime time, Pageable pageable);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE ?2 BETWEEN b.start AND b.end AND b.item.owner.id=?1 ")
    List<Booking> findCurrentBookingsByOwnerId(long ownerId, LocalDateTime time, Pageable pageable);

    @Query("SELECT new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) " +
            "FROM Booking AS b " +
            "WHERE b.status='APPROVED' AND b.end<?2 AND b.item.owner.id=?1 ")
    List<Booking> findPastBookingsByOwnerId(long ownerId, LocalDateTime time, Pageable pageable);
}
