package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ObjNotFoundException;
import ru.practicum.shareit.item.dto.CommentAddingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(MockitoExtension.class)
@TestPropertySource("/application-test.properties")
@ActiveProfiles("test")
class ItemServiceImplTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    private final ItemServiceImpl itemService;

    @InjectMocks
    private final UserService userService;

    @InjectMocks
    private final BookingService bookingService;
    private final EntityManager em;

    @Test
    public void createItem() {
        Item item = new Item();
        item.setName("Отвёртка");
        item.setDescription("Крестовая отвёртка");
        item.setAvailable(true);
        itemService.create(item, 1L);
        TypedQuery<Item> query = em.createQuery("Select i from Item AS i where i.id = :id", Item.class);
        Item itemFromDB = query.setParameter("id",4L).getSingleResult();
        assertThat(itemFromDB.getId(), notNullValue());
        assertThat(itemFromDB.getName(), equalTo(item.getName()));
        assertThat(itemFromDB.getDescription(), equalTo(item.getDescription()));
    }

    @Test
    public void updateItem() {
        Item itemUpdate = new Item();
        itemUpdate.setName("Отвёртка");
        itemUpdate.setDescription("Ржавая крестовая отвёртка");
        itemService.update(itemUpdate, 1L, 2L);
        TypedQuery<Item> query = em.createQuery("Select i from Item AS i where i.id = :id", Item.class);
        Item itemFromDB = query.setParameter("id", 2L).getSingleResult();
        assertThat(itemFromDB.getId(), notNullValue());
        assertThat(itemFromDB.getName(), equalTo(itemUpdate.getName()));
        assertThat(itemFromDB.getDescription(), equalTo(itemUpdate.getDescription()));
    }

    @Test
    public void findItemsByUserId() {
        List<Item> items = itemService.findItemsByUserId(1L, 0, 10);
        assertThat(items.size(), equalTo(2));
    }

    @Test
    public void whenFindItemsByUserIdWithParameterFromLessZero_thenCallException() {
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    List<Item> items = itemService.findItemsByUserId(1L, -1, 10);
                }
        );
        assertEquals("Параметер from не может быть меньше 0", exception.getMessage());
    }

    @Test
    public void whenFindItemsByUserIdWithParameterSizeIsZero_thenCallException() {
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    List<Item> items = itemService.findItemsByUserId(1L, 0, 0);
                }
        );
        assertEquals("Параметер size не может быть меньше 1", exception.getMessage());
    }

    @Test
    public void whenUpdateItemUserDontAccess() {
        Item itemUpdate = new Item();
        itemUpdate.setName("Отвёртка");
        itemUpdate.setDescription("Ржавая крестовая отвёртка");
        Throwable exception = assertThrows(
                ForbiddenException.class,
                () -> {
                    itemService.update(itemUpdate, 2L, 2L);
                }
        );
        assertEquals("Нет доступа для редактирования", exception.getMessage());
    }

    @Test
    public void whenUpdateWithNameIsNull_thenOldValue() {
        Item itemUpdate = new Item();
        itemUpdate.setDescription("new description");
        itemUpdate.setAvailable(false);
        Item item = itemService.update(itemUpdate, 1L, 2L);
        assertThat(item.getName(), equalTo("Молоток"));
        assertThat(item.getDescription(), equalTo("new description"));
        assertThat(item.getAvailable(), equalTo(false));
    }

    @Test
    public void whenUpdateWithDescriptionIsNull_thenOldValue() {
        Item itemUpdate = new Item();
        itemUpdate.setName("new name");
        itemUpdate.setAvailable(false);
        Item item = itemService.update(itemUpdate, 1L, 2L);
        assertThat(item.getName(), equalTo("new name"));
        assertThat(item.getDescription(), equalTo("Очень тяжелый"));
        assertThat(item.getAvailable(), equalTo(false));
    }

    @Test
    public void whenUpdateWithAvailableIsNull_thenOldValue() {
        Item itemUpdate = new Item();
        itemUpdate.setName("new name");
        itemUpdate.setDescription("new des");
        Item item = itemService.update(itemUpdate, 1L, 2L);
        assertThat(item.getName(), equalTo("new name"));
        assertThat(item.getDescription(), equalTo("new des"));
        assertThat(item.getAvailable(), equalTo(true));
    }

    @Test
    public void commentAdd() {
        itemService.addComment(1L, 2L, makeCommentAddingDto());
        TypedQuery<Comment> query = em.createQuery("Select c from Comment AS c where c.id = :id", Comment.class);
        Comment commentFromDB = query.setParameter("id", 1L).getSingleResult();
        assertThat(commentFromDB.getText(), equalTo("Комментарий"));

    }

    private CommentAddingDto makeCommentAddingDto() {
        CommentAddingDto commentAddingDto = new CommentAddingDto();
        commentAddingDto.setText("Комментарий");
        return commentAddingDto;
    }

    @Test
    public void whenFindItemByWrongId_thenCallCustomException() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    itemService.findItemById(3254L, 1L);
                }
        );
        assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    public void whenFindItemById_thenOk() {
        Item itemForCheck = new Item(1L, "Отвёртка", "Крестовая отвёртка", true);
        Item item = itemService.findItemById(1L, 1L);
        assertThat(itemForCheck.getId(), equalTo(item.getId()));
        assertThat(itemForCheck.getName(), equalTo(item.getName()));
        assertThat(itemForCheck.getDescription(), equalTo(item.getDescription()));
        assertThat(itemForCheck.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    public void whenSearchEmptyText_thenReturnEmptyList() {
        List<Item> list = itemService.search("", 0, 10);
        assertEquals(list.size(), 0);
    }

    @Test
    public void whenSearch_thenReturnListSizeIs2() {
        List<Item> list = itemService.search("Отвёртка", 0, 10);
        assertEquals(list.size(), 2);
    }

    @Test
    public void whenSearchInputParameterFromLessZero_thenCallCustomException() {
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    itemService.search("Отвёртка", -1, 10);
                }
        );
        assertEquals("Параметер from не может быть меньше 0", exception.getMessage());
    }

    @Test
    public void whenSearchInputParameterSizeLessOne_theCallCustomException() {
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    itemService.search("Отвёртка", 0, 0);
                }
        );
        assertEquals("Параметер size не может быть меньше 1", exception.getMessage());
    }

    @Test
    public void whenAddCommentInputParameterUserIdIsWrong_thenCallCustomException() {
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    itemService.addComment(1L, -1L, makeCommentAddingDto());
                }
        );
        assertEquals("Не определен заголовок X-Sharer-User-Id", exception.getMessage());
    }

    @Test
    public void whenAddCommentUserNotFound_thenCallCustomException() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    itemService.addComment(1L, 1000L, makeCommentAddingDto());
                }
        );
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void whenAddCommentItemNotFound_thenCallCustomException() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    itemService.addComment(1000L, 1L, makeCommentAddingDto());
                }
        );
        assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    public void findItemWithOneNextBooking() {
        Item item = itemService.findItemById(1L, 1L);
        assertThat(2L, equalTo(item.getNextBooking().getBookerId()));
    }
}