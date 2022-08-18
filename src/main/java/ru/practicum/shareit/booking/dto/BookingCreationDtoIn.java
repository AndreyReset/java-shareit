package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingCreationDtoIn {

    @NotNull(message = "Время старта бронирования не может быть пустым")
    private LocalDateTime start;

    @NotNull(message = "Время окончания бронирования не может быть пустым")
    private LocalDateTime end;

    @NotNull(message = "Id вещи не может быть пустым")
    private Long itemId;
}