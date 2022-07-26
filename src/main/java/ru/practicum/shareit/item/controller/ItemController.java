package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findItemsByUserId(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") long userId,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        log.info("GET зарпос на получение списка всех вещей у пользователя с id = " + userId);
        return itemService.findItemsByUserId(userId, from, size)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public ItemDto findItemById(@PathVariable long id,
                                @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1")
                                long userId) {
        log.info("GET запрос на получение вещи с id = " + id);
        return ItemMapper.toDto(itemService.findItemById(id, userId));
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemCreationDto itemCreationDto,
                          @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") long userId) {
        log.info("POST запрос на добавление вещи");
        return ItemMapper.toDto(itemService.create(ItemMapper.toItem(itemCreationDto, new User(userId)), userId));
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@Valid @RequestBody ItemUpdationDto itemUpdationDto,
                          @PathVariable long itemId,
                          @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") long userId) {
        log.info("PATCH запрос на обновление вещи с id = " + itemId);
        return ItemMapper.toDto(itemService.update(ItemMapper.toItem(itemUpdationDto), userId, itemId));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = true) String text,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "10") int size) {
        log.info("Поиск вещи по строке - `" + text + "`");
        return itemService.search(text, from, size)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentAddingDto commentAdding,
                                 @PathVariable long itemId,
                                 @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "-1") long userId) {
        log.info("Добавление комментария к вещи с id = `" + itemId + "`");
        return itemService.addComment(itemId, userId, commentAdding);
    }
}
