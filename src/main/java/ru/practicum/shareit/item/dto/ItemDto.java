package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.LastBooking;
import ru.practicum.shareit.item.model.NextBooking;
import ru.practicum.shareit.user.model.User;

import java.util.Set;

@Data
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private long request;
    private LastBooking lastBooking;
    private NextBooking nextBooking;
    private Set<CommentDto> comments;
}
