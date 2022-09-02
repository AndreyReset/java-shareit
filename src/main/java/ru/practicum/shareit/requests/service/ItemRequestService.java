package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestCreationDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestCreationDto itemRequestCreationDto, long userId);

    List<ItemRequestDto> findRequestsByOwnerId(long ownerId);

    List<ItemRequestDto> findRequestsOtherUsersWithPagination(long userId, int from, int size);

    ItemRequestDto findRequestById(long requestId, long userId);
}
