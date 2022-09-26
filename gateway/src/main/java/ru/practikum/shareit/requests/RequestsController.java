package ru.practikum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestsController {

    private final RequestsClient requestsClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody RequestCreationDto requestCreationDto) {
        log.info("Creating request with userId={}", userId);
        return requestsClient.createRequest(userId, requestCreationDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("GET requests by item owner = {}", ownerId);
        return requestsClient.getRequests(ownerId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET all requests with userId={}, from={}, size={}", userId, from, size);
        return requestsClient.getAllRequests(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long requestId) {
        log.info("GET request={}, userId={}", requestId, userId);
        return requestsClient.getRequest(userId, requestId);
    }
}
