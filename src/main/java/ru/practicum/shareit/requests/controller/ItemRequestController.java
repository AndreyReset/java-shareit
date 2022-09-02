package ru.practicum.shareit.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestCreationDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@Valid @RequestBody ItemRequestCreationDto itemRequestCreationDto,
                                 @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("POST - добавить запроса на вещь");
        return itemRequestService.create(itemRequestCreationDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> findRequestsByOwnerId(@RequestHeader(value = "X-Sharer-User-Id") long ownerId) {
        log.info("GET - получить список запросов пользователя с id = `" + ownerId + "`");
        return itemRequestService.findRequestsByOwnerId(ownerId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findRequestsOtherUsersWithPagination(
                                                @RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("GET - получить список запросов всех пользователей с " +
                "элемента `" + from + "` в количестве `" + size + "`");
        return itemRequestService.findRequestsOtherUsersWithPagination(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto findRequestById(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                          @PathVariable long requestId) {
        log.info("GET - получить запрос с id = `" + requestId + "`");
        return itemRequestService.findRequestById(requestId, userId);
    }
}
