package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.requests.ItemRequestsMapper.toDto;
import static ru.practicum.shareit.requests.ItemRequestsMapper.toItemRequest;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestsRepository itemRequestsRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestDto itemRequestDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjNotFoundException("Пользователь не найден"));
        return toDto(itemRequestsRepository.save(
                toItemRequest(itemRequestDto, user, LocalDateTime.now())
        ));
    }

    @Override
    public List<ItemRequestDto> findRequestsByOwnerId(long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new ObjNotFoundException("Пользователь не найден"));
        List<ItemRequest> itemRequests = itemRequestsRepository.findAllByRequester_id(
                ownerId, Sort.by(Sort.Direction.DESC, "created"));
        return itemRequests.stream()
                .map(this::addItemShortData)
                .map(ItemRequestsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findRequestsOtherUsersWithPagination(long userId, int from, int size) {
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
        return toDto(addItemShortData(itemRequest));
    }

    private ItemRequest addItemShortData(ItemRequest itemRequest) {
        itemRequest.setItems(itemRequestsRepository.findItemShortDataForItemRequestById(itemRequest.getId()));
        return itemRequest;
    }
}
