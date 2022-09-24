package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatusForFind;

import java.util.List;

public interface BookingService {

    Booking create(Booking booking, long userId);

    Booking update(long bookingId, long userId, Boolean approved);

    Booking findBookingById(long bookingId, long userId);

    List<Booking> findBookingsByUserId(long userId, BookingStatusForFind state, int from, int size);

    List<Booking> findBookingsByOwner(long userId, BookingStatusForFind state, int from, int size);
}
