package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.*;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                             @RequestBody BookingDto bookingDto) {
        log.info("POST - добавление нового запроса на бронирование с userId={}", userId);
        return toDto(bookingService.create(toBooking(bookingDto, new Item(), new User()), userId));
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingDto update(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                             @PathVariable long bookingId,
                             @RequestParam Boolean approved) {
        log.info("PATCH - подтвердить/отклонить запрос на бронирование, approved = {}", approved);
        return toDto(bookingService.update(bookingId, userId, approved));
    }

    @GetMapping (value = "/{bookingId}")
    public BookingDto findBookingById(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                      @PathVariable int bookingId) {
        log.info("GET - получение вещи по id={}, userId={}", bookingId, userId);
        return toDto(bookingService.findBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> findBookingsByUserId(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                 @RequestParam BookingStatusForFind state,
                                                 @RequestParam int from,
                                                 @RequestParam int size) {
        log.info("GET - список бронирований с state={}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingService.findBookingsByUserId(userId, state, from, size)
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/owner")
    public List<BookingDto> findBookingsByOwner(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                @RequestParam BookingStatusForFind state,
                                                @RequestParam int from,
                                                @RequestParam int size) {
        log.info("GET - список вещей с state={}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingService.findBookingsByOwner(userId, state, from, size)
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
