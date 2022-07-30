package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<Item> findItemsByUserId(long userId) {
        return itemRepository.findItemsByUserId(userId);
    }

    @Override
    public Optional<Item> findItemById(long id) {
        return itemRepository.findItemById(id);
    }

    @Override
    public Item create(Item item, long userId) {
        if (userId == -1) throw new BadRequestException("Не определен заголовок X-Sharer-User-Id");
        if (userRepository.findUserById(userId).isEmpty())
            throw new ObjNotFoundException("Пользователь не найден");
        return itemRepository.create(item, userId);
    }

    @Override
    public Item update(Item item, long userId, long itemId) {
        if (userId == -1) throw new BadRequestException("Не определен заголовок X-Sharer-User-Id");
        return itemRepository.update(item, userId, itemId);
    }

    @Override
    public List<Item> search(String text) {
        return itemRepository.search(text);
    }
}
