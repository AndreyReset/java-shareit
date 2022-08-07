package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.requests.model.ItemRequest;

@Data
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long owner;
    private ItemRequest request;
}
