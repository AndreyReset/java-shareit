package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemMapper.toDto;
import static ru.practicum.shareit.item.ItemMapper.toItem;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findItemsByUserId(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                           @RequestParam int from,
                                           @RequestParam int size) {
        log.info("GET - получить список вещей у пользователя с id={}, from={}, size={}", userId, from, size);
        return itemService.findItemsByUserId(userId, from, size)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public ItemDto findItemById(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                @PathVariable long id) {
        log.info("GET запрос на получение вещи с id={}, userId={}", id, userId);
        return toDto(itemService.findItemById(id, userId));
    }

    @PostMapping
    public ItemDto create(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("POST запрос на добавление вещи, userId={}", userId);
        return toDto(itemService.create(toItem(itemDto, new User(userId)), userId));
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable long itemId) {
        log.info("PATCH запрос на обновление вещи с id = " + itemId);
        return toDto(itemService.update(toItem(itemDto), userId, itemId));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @RequestParam int from,
                                @RequestParam int size) {
        log.info("Поиск вещи по строке - `{}`, from={}, size={}", text, from, size);
        return itemService.search(text, from, size)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Добавление комментария к вещи с id=`{}`, userId={}", itemId, userId);
        return itemService.addComment(itemId, userId, commentDto);
    }
}
