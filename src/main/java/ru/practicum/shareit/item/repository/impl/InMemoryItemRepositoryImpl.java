package ru.practicum.shareit.item.repository.impl;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ObjNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryItemRepositoryImpl implements ItemRepository {

    private final List<Item> items = new ArrayList<>();
    private long itemId = 1;

    @Override
    public List<Item> findItemsByUserId(long userId) {
        return items.stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findItemById(long id) {
        return items.stream()
                .filter(it -> it.getId() == id)
                .findFirst();
    }

    @Override
    public Item create(Item item, long userId) {
        item.setId(itemId);
        item.setOwner(userId);
        items.add(item);
        itemId++;
        return item;
    }

    @Override
    public Item update(Item item, long userId, long itemId) {
        for (Item it : items) {
            if (it.getId() == itemId) {
                if (it.getOwner() != userId)
                    throw new ForbiddenException("Нет доступа для редактирования");
                if (StringUtils.hasText(item.getName()))
                    it.setName(item.getName());
                if (StringUtils.hasText(item.getDescription()))
                    it.setDescription(item.getDescription());
                if (item.getAvailable() != null)
                    it.setAvailable(item.getAvailable());
                return it;
            }
        }
        throw new ObjNotFoundException("Вещь для редактирования не найдена");
    }

    @Override
    public List<Item> search(String text) {
        return items.stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                                !text.isEmpty())
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
