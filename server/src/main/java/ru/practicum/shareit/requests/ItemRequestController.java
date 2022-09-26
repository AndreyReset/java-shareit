package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    public ItemRequestDto create(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST - добавить запроса на вещь");
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> findRequestsByOwnerId(@RequestHeader(value = "X-Sharer-User-Id") long ownerId) {
        log.info("GET - получить список запросов пользователя с id = {}", ownerId);
        return itemRequestService.findRequestsByOwnerId(ownerId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findRequestsOtherUsersWithPagination(
                                                @RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                @RequestParam int from,
                                                @RequestParam int size) {
        log.info("GET - список всех запросов, userId={}, from={}, size={}", userId, from, size);
        return itemRequestService.findRequestsOtherUsersWithPagination(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto findRequestById(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                          @PathVariable long requestId) {
        log.info("GET - получить запрос с id={}, userId={}", requestId, userId);
        return itemRequestService.findRequestById(requestId, userId);
    }
}
