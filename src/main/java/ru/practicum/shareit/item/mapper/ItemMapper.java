package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner());
        itemDto.setRequestId(item.getRequestId());
        itemDto.setLastBooking(item.getLastBooking());
        itemDto.setNextBooking(item.getNextBooking());
        itemDto.setComments(CommentMapper.toDto(item));
        return itemDto;
    }

    public static Item toItem(ItemCreationDto itemCreationDto, User owner) {
        Item item = new Item();
        item.setName(itemCreationDto.getName());
        item.setDescription(itemCreationDto.getDescription());
        item.setAvailable(itemCreationDto.isAvailable());
        item.setOwner(owner);
        item.setRequestId(itemCreationDto.getRequestId());
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
