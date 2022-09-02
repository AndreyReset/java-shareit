package ru.practicum.shareit.requests.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemShortData {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long requestId;
    private long ownerId;
}
