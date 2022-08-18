package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDtoIn;
import ru.practicum.shareit.booking.dto.BookingCreationDtoOut;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatusForFind;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingCreationDtoOut create(
            @Valid @RequestBody BookingCreationDtoIn bookingCreationDtoIn,
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") long userId) {
        log.info("POST - добавление нового запроса на бронирование");
        Booking booking = BookingMapper.toBooking(bookingCreationDtoIn);
        return BookingMapper.creationDtoOut(bookingService.create(booking, userId));
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingDto update(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") long userId,
            @PathVariable long bookingId,
            @RequestParam(required = false) Boolean approved) {
        log.info("PATCH - подтвердить/отклонить запрос на бронирование, approved = " + approved);
        return BookingMapper.toDto(bookingService.update(bookingId, userId, approved));
    }

    @GetMapping (value = "/{bookingId}")
    public BookingDto findBookingById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") long userId,
            @PathVariable int bookingId) {
        log.info("GET - получение вещи по id = `" + bookingId + "`, userId = `" + userId + "`");
        return BookingMapper.toDto(bookingService.findBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> findBookingsByUserId(
            @RequestParam(defaultValue = "ALL") BookingStatusForFind state,
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") long userId) {
        log.info("GET - получение списка бронирований со статусом = `" + state +
                "`, пользователя = `" + userId + "`");
        return bookingService.findBookingsByUserId(userId, state)
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/owner")
    public List<BookingDto> findBookingsByOwner(
            @RequestParam(defaultValue = "ALL") BookingStatusForFind state,
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") long userId) {
        log.info("GET - получение списка бронирований вещей со статусом = `" + state +
                "`, пользователя = `" + userId + "`");
        return bookingService.findBookingsByOwner(userId, state)
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
