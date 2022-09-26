package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> findRequestsByOwnerId(long ownerId);

    List<ItemRequestDto> findRequestsOtherUsersWithPagination(long userId, int from, int size);

    ItemRequestDto findRequestById(long requestId, long userId);
}
