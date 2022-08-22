package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentAddingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> findItemsByUserId(long userId);

    Item findItemById(long id, long userId);

    Item create(Item item, long userId);

    Item update(Item item, long userId, long itemId);

    List<Item> search(String text);

    CommentDto addComment(long itemId, long userId, CommentAddingDto comment);
}
