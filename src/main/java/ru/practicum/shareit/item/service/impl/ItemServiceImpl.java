package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ObjNotFoundException;
import ru.practicum.shareit.item.dto.CommentAddingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.LastBooking;
import ru.practicum.shareit.item.model.NextBooking;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<Item> findItemsByUserId(long userId, int from, int size) {
        verifyInputDataForPageable(from, size);
        List<Item> items = itemRepository.findAllByOwner_idIs(userId, PageRequest.of(from, size));
        for (Item item: items) {
            addNextBookingToItem(item, userId);
            addLastBookingToItem(item, userId);
            addCommentsToItem(item);
        }
        return items;
    }

    @Override
    public Item findItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjNotFoundException("Вещь не найдена"));
        addNextBookingToItem(item, userId);
        addLastBookingToItem(item, userId);
        addCommentsToItem(item);
        return item;
    }

    private void addNextBookingToItem(Item item, long userId) {
        List<NextBooking> nextBooking = itemRepository.findNextBooking(item.getId(), LocalDateTime.now(), userId,
                PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "start")));
        if (!nextBooking.isEmpty())
            item.setNextBooking(nextBooking.get(0));
    }

    private void addLastBookingToItem(Item item, long userId) {
        List<LastBooking> lastBooking = itemRepository.findLastBooking(item.getId(), LocalDateTime.now(), userId,
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")));
        if (!lastBooking.isEmpty())
            item.setLastBooking(lastBooking.get(0));
    }

    private void addCommentsToItem(Item item) {
        Set<Comment> comments = new HashSet<>(commentRepository.findAllByItem(item));
        item.setComment(comments);
    }

    @Override
    @Transactional
    public Item create(Item item, long userId) {
        verifyInputParameterUserId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjNotFoundException("Пользователь не найден"));
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item update(Item item, long userId, long itemId) {
        verifyInputParameterUserId(userId);
        Optional<Item> oldItem = Optional.ofNullable(findItemById(itemId, userId));
        if (oldItem.isPresent()) {
            if (userId != oldItem.get().getOwner().getId())
                throw new ForbiddenException("Нет доступа для редактирования");
            item.setId(itemId);
            if (item.getName() == null)
                item.setName(oldItem.get().getName());
            if (item.getDescription() == null)
                item.setDescription(oldItem.get().getDescription());
            if (item.getAvailable() == null)
                item.setAvailable(oldItem.get().getAvailable());
            item.setOwner(oldItem.get().getOwner());
            item.setRequestId(oldItem.get().getRequestId());
        }
        return itemRepository.save(item);
    }

    @Override
    public List<Item> search(String text, int from, int size) {
        verifyInputDataForPageable(from, size);
        Pageable pageable = PageRequest.of(from, size);
        if (text.isBlank()) return new ArrayList<>();
        return itemRepository.findItemsByNameAndDescriptionAndAvailable(text, pageable);
    }

    @Override
    @Transactional
    public CommentDto addComment(long itemId, long userId, CommentAddingDto commentAddingDto) {
        verifyInputParameterUserId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjNotFoundException("Вещь не найдена"));
        List<Booking> booking = bookingRepository
                .findAllByItemAndBookerAndStatusAndEndLessThan(item, user, BookingStatus.APPROVED, LocalDateTime.now());
        if (booking.isEmpty()) throw new BadRequestException("Ошибочный запрос");

        return CommentMapper.toDto(commentRepository.save(CommentMapper.toComment(commentAddingDto, item, user)));
    }

    private void verifyInputDataForPageable(int from, int size) {
        if (size <= 0) throw new BadRequestException("Параметер size не может быть меньше 1");
        if (from < 0) throw new BadRequestException("Параметер from не может быть меньше 0");
    }

    private void  verifyInputParameterUserId(long userId) {
        if (userId == -1) throw new BadRequestException("Не определен заголовок X-Sharer-User-Id");
    }
}
