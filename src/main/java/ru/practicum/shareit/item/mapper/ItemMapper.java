package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdationDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner());
        itemDto.setRequest(item.getRequest());
        return itemDto;
    }

    public static Item toItem(ItemCreationDto itemCreationDto) {
        Item item = new Item();
        item.setName(itemCreationDto.getName());
        item.setDescription(itemCreationDto.getDescription());
        item.setAvailable(itemCreationDto.isAvailable());
        item.setOwner(itemCreationDto.getOwner());
        return item;
    }

    public static Item toItem(ItemUpdationDto itemUpdationDto) {
        Item item = new Item();
        item.setName(itemUpdationDto.getName());
        item.setDescription(itemUpdationDto.getDescription());
        item.setAvailable(itemUpdationDto.getAvailable());
        return item;
    }
}
