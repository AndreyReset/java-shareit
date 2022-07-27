package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdationDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") long userId) {
        log.info("GET зарпос на получение списка всех вещей у пользователя с id = " + userId);
        return itemService.findAll()
                .stream()
                .filter(it -> it.getOwner() == userId)
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public Optional<ItemDto> findItemById(@PathVariable long id) {
        log.info("GET запрос на получение вещи с id = " + id);
        return itemService.findItemById(id)
                .map(ItemMapper::toDto);
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemCreationDto itemCreationDto,
                          @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") long userId) {
        log.info("POST запрос на добавление вещи");
        if (userId == -1) throw new BadRequestException("Не определен заголовок X-Sharer-User-Id");
        Item item = ItemMapper.toItem(itemCreationDto);
        item.setOwner(userId);
        return ItemMapper.toDto(itemService.create(item));
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@Valid @RequestBody ItemUpdationDto itemUpdationDto,
                          @PathVariable long itemId,
                          @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") long userId) {
        log.info("PATCH запрос на обновление вещи");
        if (userId == -1) throw new BadRequestException("Не определен заголовок X-Sharer-User-Id");
        Item item = ItemMapper.toItem(itemUpdationDto);
        item.setId(itemId);
        item.setOwner(userId);
        return ItemMapper.toDto(itemService.update(item));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required=true) String text) {
        log.info("Поиск вещи по строке - " + text);
        return itemService.search(text)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
