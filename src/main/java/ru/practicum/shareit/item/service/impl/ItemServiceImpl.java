package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ObjNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<Item> findItemsByUserId(long userId) {
        return itemRepository.findItemsByOwnerIs(userId);
    }

    @Override
    public Optional<Item> findItemById(long id) {
        return Optional.ofNullable(itemRepository.findById(id)
                .orElseThrow(() -> new ObjNotFoundException("Вещь не найдена")));
    }

    @Override
    public Item create(Item item, long userId) {
        if (userId == -1) throw new BadRequestException("Не определен заголовок X-Sharer-User-Id");
        if (userRepository.findById(userId).isEmpty())
            throw new ObjNotFoundException("Пользователь не найден");
        item.setOwner(userId);
        return itemRepository.save(item);
    }

    @Override
    public Item update(Item item, long userId, long itemId) {
        if (userId == -1) throw new BadRequestException("Не определен заголовок X-Sharer-User-Id");
        Optional<Item> oldItem = findItemById(itemId);
        if (oldItem.isPresent()) {
            if (userId != oldItem.get().getOwner())
                throw new ForbiddenException("Нет доступа для редактирования");
            item.setId(itemId);
            if (item.getName() == null)
                item.setName(oldItem.get().getName());
            if (item.getDescription() == null)
                item.setDescription(oldItem.get().getDescription());
            if (item.getAvailable() == null)
                item.setAvailable(oldItem.get().getAvailable());
            item.setOwner(oldItem.get().getOwner());
            item.setRequest(oldItem.get().getRequest());
        }
        return itemRepository.save(item);
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) return new ArrayList<>();
        return itemRepository.findItemsByAvailableIsAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(
                true, text, text);
    }
}
