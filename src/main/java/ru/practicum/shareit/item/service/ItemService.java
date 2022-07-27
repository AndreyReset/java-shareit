package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<Item> findAll();

    Optional<Item> findItemById(long id);

    Item create(Item item);

    Item update(Item item);

    List<Item> search(String text);
}
