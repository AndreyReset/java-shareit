package ru.practicum.shareit.item.repository.impl;

import org.springframework.stereotype.Component;
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
    public List<Item> findAll() {
        return items;
    }

    @Override
    public Optional<Item> findItemById(long id) {
        return items.stream()
                .filter(it -> it.getId() == id)
                .findFirst();
    }

    @Override
    public Item create(Item item) {
        item.setId(itemId);
        items.add(item);
        itemId++;
        return item;
    }

    @Override
    public Item update(Item item) {
        for (Item it : items) {
            if (it.getId() == item.getId()) {
                if (it.getOwner() != item.getOwner())
                    throw new ForbiddenException("Нет доступа для редактирования");
                if (item.getName() != null)
                    if (!item.getName().isEmpty())
                        it.setName(item.getName());
                if (item.getDescription() != null)
                    if (!item.getDescription().isEmpty())
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
