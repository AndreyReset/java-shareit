package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatusForFind;

import java.util.List;

public interface BookingService {

    Booking create(Booking booking, long userId);

    Booking update(long bookingId, long userId, Boolean approved);

    Booking findBookingById(long bookingId, long userId);

    List<Booking> findBookingsByUserId(long userId, BookingStatusForFind state);

    List<Booking> findBookingsByOwner(long userId, BookingStatusForFind state);
}
