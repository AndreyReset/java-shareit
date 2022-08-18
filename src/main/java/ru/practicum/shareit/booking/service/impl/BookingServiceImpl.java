package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingStatusForFind;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking create(Booking booking, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new ObjNotFoundException("Вещь не найдена"));
        if (!item.getAvailable())
            throw new BadRequestException("Вещь недоступна");
        if (item.getOwner() == userId)
            throw new ObjNotFoundException("Владелец вещи не может забронировать свою вещь");
        booking.setBooker(user);
        booking.setItem(item);
        if (booking.getStart().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Начало бронирования не может быть в прошлом");
        return bookingRepository.save(booking);
    }

    @Override
    public Booking update(long bookingId, long userId, Boolean approved) {
        if (userId == -1) throw new BadRequestException("Не определен заголовок X-Sharer-User-Id");
        if (approved == null) throw new BadRequestException("Параметр approved не определен");
        Booking booking = bookingRepository.findBookingByIdForUpdate(bookingId, userId)
                .orElseThrow(() -> new ObjNotFoundException("Объект для обновления не найден"));
        if (approved) {
            if (booking.getStatus().equals(BookingStatus.APPROVED))
                throw new BadRequestException("Статус изменен ранее на APPROVED");
            booking.setStatus(BookingStatus.APPROVED);
        }
        else booking.setStatus(BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking findBookingById(long bookingId, long userId) {
        if (userId == -1) throw new BadRequestException("Не определен заголовок X-Sharer-User-Id");
        return bookingRepository.findBookingByBookerIdOrOwnerId(bookingId, userId)
                .orElseThrow(() -> new ObjNotFoundException("Запись о бронировании не найдена"));
    }

    @Override
    public List<Booking> findBookingsByUserId(long userId, BookingStatusForFind state) {
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllBookingsByBookerId(userId);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case WAITING:
                bookings = bookingRepository.findBookingsByStatusIs(userId, BookingStatus.WAITING);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case REJECTED:
                bookings = bookingRepository.findBookingsByStatusIs(userId, BookingStatus.REJECTED);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case PAST:
                bookings = bookingRepository.findPastBookings(userId, LocalDateTime.now());
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case FUTURE:
                bookings = bookingRepository.findFutureBookings(userId, LocalDateTime.now());
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookings(userId, LocalDateTime.now());
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<Booking> findBookingsByOwner(long userId, BookingStatusForFind state) {
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllBookingByOwnerId(userId);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case WAITING:
                bookings = bookingRepository.findBookingsByOwnerIdAndStatus(userId, BookingStatus.WAITING);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case REJECTED:
                bookings = bookingRepository.findBookingsByOwnerIdAndStatus(userId, BookingStatus.REJECTED);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case PAST:
                bookings = bookingRepository.findPastBookingsByOwnerId(userId, LocalDateTime.now());
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByOwnerId(userId, LocalDateTime.now());
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByOwnerId(userId, LocalDateTime.now());
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
