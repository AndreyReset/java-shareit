package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestCreationDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.mapper.ItemRequestsMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestsRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestsRepository itemRequestsRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestCreationDto itemRequestCreationDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjNotFoundException("Пользователь не найден"));
        return ItemRequestsMapper.toDto(itemRequestsRepository.save(
                ItemRequestsMapper.toItemRequest(itemRequestCreationDto, user, LocalDateTime.now())
        ));
    }

    @Override
    public List<ItemRequestDto> findRequestsByOwnerId(long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new ObjNotFoundException("Пользователь не найден"));
        List<ItemRequest> itemRequests = itemRequestsRepository.findRequestsByOwnerId(
                ownerId, Sort.by(Sort.Direction.DESC, "created"));
        return itemRequests.stream()
                .map(this::addItemShortData)
                .map(ItemRequestsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findRequestsOtherUsersWithPagination(long userId, int from, int size) {
        if (size <= 0) throw new BadRequestException("Параметер size не может быть меньше 1");
        if (from < 0) throw new BadRequestException("Параметер from не может быть меньше 0");
        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"));
        return itemRequestsRepository.findRequestsOtherUsersWithPagination(userId, pageable)
                .map(this::addItemShortData)
                .map(ItemRequestsMapper::toDto)
                .toList();
    }

    @Override
    public ItemRequestDto findRequestById(long requestId, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjNotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = itemRequestsRepository.findById(requestId)
                                    .orElseThrow(() -> new ObjNotFoundException("Запрос не найден"));
        return ItemRequestsMapper.toDto(addItemShortData(itemRequest));
    }

    private ItemRequest addItemShortData(ItemRequest itemRequest) {
        itemRequest.setItems(itemRequestsRepository.findItemShortDataForItemRequestById(itemRequest.getId()));
        return itemRequest;
    }
}
