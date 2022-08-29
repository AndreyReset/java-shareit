package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
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
    public List<Item> findItemsByUserId(long userId) {
        List<Item> items = itemRepository.findItemsByOwnerIsOrderByIdAsc(userId);
        for (Item item: items) {
            addNextBookingToItem(item, userId);
            addlastBookingToItem(item, userId);
            addCommentsToItem(item);
        }
        return items;
    }

    @Override
    public Item findItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjNotFoundException("Вещь не найдена"));
        addNextBookingToItem(item, userId);
        addlastBookingToItem(item, userId);
        addCommentsToItem(item);
        return item;
    }

    private void addNextBookingToItem(Item item, long userId) {
        List<NextBooking> nextBooking = itemRepository.findNextBooking(
                                        item.getId(), LocalDateTime.now(), userId);
        if (!nextBooking.isEmpty())
            item.setNextBooking(nextBooking.get(0));
    }

    private void addlastBookingToItem(Item item, long userId) {
        List<LastBooking> lastBooking = itemRepository.findLastBooking(
                                        item.getId(), LocalDateTime.now(), userId);
        if (!lastBooking.isEmpty())
            item.setLastBooking(lastBooking.get(0));
    }

    private void addCommentsToItem(Item item) {
        Set<Comment> comments = new HashSet<>(commentRepository.findCommentsToItemById(item.getId()));
        item.setComment(comments);
    }

    @Override
    @Transactional
    public Item create(Item item, long userId) {
        if (userId == -1) throw new BadRequestException("Не определен заголовок X-Sharer-User-Id");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjNotFoundException("Пользователь не найден"));
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item update(Item item, long userId, long itemId) {
        if (userId == -1) throw new BadRequestException("Не определен заголовок X-Sharer-User-Id");
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
            item.setRequest(oldItem.get().getRequest());
        }
        return itemRepository.save(item);
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) return new ArrayList<>();
        return itemRepository.findItemsByNameAndDescriptionAndAvailable(text);
    }

    @Override
    @Transactional
    public CommentDto addComment(long itemId, long userId, CommentAddingDto commentAddingDto) {
        if (userId == -1) throw new BadRequestException("Не определен заголовок X-Sharer-User-Id");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjNotFoundException("Вещь не найдена"));
        List<Booking> booking = bookingRepository.findBookigsToCheckForAddingAComment(
                        itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        if (booking.isEmpty()) throw new BadRequestException("Ошибочный запрос");

        return CommentMapper.toDto(commentRepository.save(CommentMapper.toComment(commentAddingDto, item, user)));
    }
}
