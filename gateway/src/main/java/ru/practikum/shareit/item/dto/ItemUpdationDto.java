package ru.practikum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemUpdationDto {
    private String name;
    private String description;
    private Boolean available;
}