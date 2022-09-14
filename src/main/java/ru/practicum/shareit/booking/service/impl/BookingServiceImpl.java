package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingStatusForFind;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.pageable.OffsetLimitPageable;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking create(Booking booking, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new ObjNotFoundException("Вещь не найдена"));
        if (!item.getAvailable())
            throw new BadRequestException("Вещь недоступна");
        if (item.getOwner().getId() == userId)
            throw new ObjNotFoundException("Владелец вещи не может забронировать свою вещь");
        booking.setBooker(user);
        booking.setItem(item);
        if (booking.getStart().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Начало бронирования не может быть в прошлом");
        if (booking.getEnd().isBefore(booking.getStart()))
            throw new BadRequestException("Конец бронирования не может быть больше или равен началу бронирования");
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking update(long bookingId, long userId, Boolean approved) {
        if (approved == null) throw new BadRequestException("Параметр approved не определен");
        Booking booking = bookingRepository.findBookingByIdForUpdate(bookingId, userId)
                .orElseThrow(() -> new ObjNotFoundException("Объект для обновления не найден"));
        if (approved) {
            if (booking.getStatus().equals(BookingStatus.APPROVED))
                throw new BadRequestException("Статус изменен ранее на APPROVED");
            booking.setStatus(BookingStatus.APPROVED);
        } else booking.setStatus(BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking findBookingById(long bookingId, long userId) {
        return bookingRepository.findBookingByBookerIdOrOwnerId(bookingId, userId)
                .orElseThrow(() -> new ObjNotFoundException("Запись о бронировании не найдена"));
    }

    @Override
    public List<Booking> findBookingsByUserId(long userId, BookingStatusForFind state, int from, int size) {
        Pageable pageable = OffsetLimitPageable.of(from, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllBookingsByBookerId(userId, pageable);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case WAITING:
                bookings = bookingRepository.findBookingsByStatusIs(userId, BookingStatus.WAITING, pageable);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case REJECTED:
                bookings = bookingRepository.findBookingsByStatusIs(userId, BookingStatus.REJECTED, pageable);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case PAST:
                bookings = bookingRepository.findPastBookings(userId, LocalDateTime.now(), pageable);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case FUTURE:
                bookings = bookingRepository.findFutureBookings(userId, LocalDateTime.now(), pageable);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookings(userId, LocalDateTime.now(), pageable);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<Booking> findBookingsByOwner(long userId, BookingStatusForFind state, int from, int size) {
        Pageable pageable = OffsetLimitPageable.of(from, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllBookingByOwnerId(userId, pageable);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case WAITING:
                bookings = bookingRepository.findBookingsByOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case REJECTED:
                bookings = bookingRepository.findBookingsByOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case PAST:
                bookings = bookingRepository.findPastBookingsByOwnerId(userId, LocalDateTime.now(), pageable);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByOwnerId(userId, LocalDateTime.now(), pageable);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByOwnerId(userId, LocalDateTime.now(), pageable);
                if (bookings.isEmpty()) throw new ObjNotFoundException("Записи не найдены");
                else return bookings;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
