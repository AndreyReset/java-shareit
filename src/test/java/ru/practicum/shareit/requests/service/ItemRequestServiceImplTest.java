package ru.practicum.shareit.requests.service;

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
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestCreationDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestsRepository;
import ru.practicum.shareit.user.repository.UserRepository;

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
class ItemRequestServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRequestsRepository itemRequestsRepository;

    @InjectMocks
    private final ItemRequestServiceImpl itemRequestService;

    private final EntityManager em;

    @Test
    public void createWithUserIdIsNotFound() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    itemRequestService.create(new ItemRequestCreationDto(), 150L);
                }
        );
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void create() {
        ItemRequestCreationDto itemRequestCreationDto = new ItemRequestCreationDto();
        itemRequestCreationDto.setDescription("необходимо щётка для обуви, мягкая");
        ItemRequestDto requestDto = itemRequestService.create(itemRequestCreationDto, 1L);

        TypedQuery<ItemRequest> query = em.createQuery(
                "Select ш from ItemRequest AS ш where ш.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id",3L).getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(requestDto.getDescription()));
    }

    @Test
    public void findRequestsByOwnerId_ownerNotFound() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    itemRequestService.findRequestsByOwnerId(150L);
                }
        );
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void findRequestsByOwnerId() {
        List<ItemRequestDto> list = itemRequestService.findRequestsByOwnerId(1L);
        assertThat(list.size(), equalTo(1));
    }

    @Test
    public void findRequestsOtherUsersWithPagination_inputParameterSizeLessOne() {
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    itemRequestService.findRequestsOtherUsersWithPagination(1L, 0, 0);
                }
        );
        assertEquals("Параметер size не может быть меньше 1", exception.getMessage());
    }

    @Test
    public void findRequestsOtherUsersWithPagination_inputParameterFromLessZero() {
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    itemRequestService.findRequestsOtherUsersWithPagination(1L, -1, 1);
                }
        );
        assertEquals("Параметер from не может быть меньше 0", exception.getMessage());
    }

    @Test
    public void findRequestsWithPagination() {
        List<ItemRequestDto> list = itemRequestService.findRequestsOtherUsersWithPagination(1L, 0, 10);
        assertThat(list.size(), equalTo(1));
    }

    @Test
    public void whenFindRequestByIdWrongUser() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    itemRequestService.findRequestById(1L, 1000L);
                }
        );
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void whenFindRequestByWrongId() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    itemRequestService.findRequestById(10000L, 1L);
                }
        );
        assertEquals("Запрос не найден", exception.getMessage());
    }

    @Test
    public void findRequestById() {
        ItemRequestDto requestDto = itemRequestService.findRequestById(1L, 1L);
        assertThat(requestDto.getId(), equalTo(1L));
    }
}