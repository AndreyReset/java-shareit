package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> findItemsByUserId(long userId);

    Optional<Item> findItemById(long id);

    Item create(Item item, long userId);

    Item update(Item item, long userId, long itemId);

    List<Item> search(String text);
}
