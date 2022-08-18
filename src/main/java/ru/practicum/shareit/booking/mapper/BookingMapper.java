package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingCreationDtoIn;
import ru.practicum.shareit.booking.dto.BookingCreationDtoOut;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

public class BookingMapper {

    public static BookingDto toDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setItem(booking.getItem());
        bookingDto.setBooker(booking.getBooker());
        return bookingDto;
    }

    public static Booking toBooking(BookingCreationDtoIn bookingCreationDto) {
        Booking booking = new Booking();
        Item item = new Item();
        item.setId(bookingCreationDto.getItemId());
        booking.setStart(bookingCreationDto.getStart());
        booking.setEnd(bookingCreationDto.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        return booking;
    }

    public static BookingCreationDtoOut creationDtoOut(Booking booking) {
        BookingCreationDtoOut bookingCreationDtoOut = new BookingCreationDtoOut();
        bookingCreationDtoOut.setId(booking.getId());
        return bookingCreationDtoOut;
    }
}
