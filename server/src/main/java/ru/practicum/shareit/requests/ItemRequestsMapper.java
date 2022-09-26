package ru.practicum.shareit.requests;

import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class ItemRequestsMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user, LocalDateTime time) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(user)
                .created(time)
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems())
                .build();
    }
}
