package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<Item> findItemsByUserId(long userId);

    Item findItemById(long id, long userId);

    Item create(Item item, long userId);

    Item update(Item item, long userId, long itemId);

    List<Item> search(String text);

    Comment addComment(long itemId, long userId, Comment comment);
}
