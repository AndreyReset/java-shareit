package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.LastBooking;
import ru.practicum.shareit.item.model.NextBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByOwnerIsOrderByIdAsc(long userId);

    List<Item> findItemsByAvailableIsAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(
            Boolean available, String description, String name);

    @Query("SELECT new ru.practicum.shareit.item.model.NextBooking(b.id, b.booker.id) " +
            "FROM Booking AS b " +
            "WHERE b.item.id=?1 AND b.start>?2 AND b.status<>'REJECTED' AND b.item.owner=?3 " +
            "ORDER BY b.start ASC "
    )
    List<NextBooking> findNextBooking(long itemId, LocalDateTime time, long ownerId);

    @Query("SELECT new ru.practicum.shareit.item.model.LastBooking(b.id, b.booker.id) " +
            "FROM Booking AS b " +
            "WHERE b.item.id=?1 AND b.start<?2 AND b.status<>'REJECTED' AND b.item.owner=?3 " +
            "ORDER BY b.start DESC "
    )
    List<LastBooking> findLastBooking(long itemId, LocalDateTime time, long ownerId);
}
