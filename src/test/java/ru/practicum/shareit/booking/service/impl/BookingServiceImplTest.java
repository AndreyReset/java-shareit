package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingStatusForFind;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(MockitoExtension.class)
@TestPropertySource("/application-test.properties")
@ActiveProfiles("test")
class BookingServiceImplTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    private final BookingServiceImpl bookingService;

    private final EntityManager em;

    @Test
    public void whenCreateBookingWhereItemNotAvailable_thenCallCustomException() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(false);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);

        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    bookingService.create(booking, 1L);
                }
        );
        assertEquals("Вещь недоступна", exception.getMessage());
    }

    @Test
    public void whenCreateBookingWhereOwnerItemEqualBooker_thenCallCustomException() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        User user = new User(1, "Василий", "email@ya.ru");
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(user);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);

        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.create(booking, 1L);
                }
        );
        assertEquals("Владелец вещи не может забронировать свою вещь", exception.getMessage());
    }

    @Test
    public void whenCreate_thenOk() {
        Booking bookingCreate = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1L),
                LocalDateTime.now().plusHours(1L),
                new Item(1L, "name", "des", true),
                new User(),
                BookingStatus.WAITING
        );
        Booking booking = bookingService.create(bookingCreate, 2L);

        assertThat(bookingCreate.getBooker().getId(), equalTo(2L));
    }

    @Test
    public void whenCreateBookingWhereStartTimeBeforeTimeNow_thenCallCustomException() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        User user = new User(1, "Василий", "email@ya.ru");
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(user);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(1L));

        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    bookingService.create(booking, 2L);
                }
        );
        assertEquals("Начало бронирования не может быть в прошлом", exception.getMessage());
    }

    @Test
    public void whenUpdateBookingWhereApprovedNull_thenCallCustomException() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    bookingService.update(1L, 1L, null);
                }
        );
        assertEquals("Параметр approved не определен", exception.getMessage());
    }

    @Test
    public void whenUpdateStatusApprovedBeforeRequesting_thenCallCustomException() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        Mockito
                .when(bookingRepository.findBookingByIdForUpdate(1L, 1L))
                .thenReturn(Optional.empty());

        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.update(1L, 1L, true);
                }
        );
        assertEquals("Объект для обновления не найден", exception.getMessage());
    }

    @Test
    public void whenUpdateBookingItemIsNotFound_thenCallCustomException() {
        BookingServiceImpl bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.APPROVED);
        Mockito
                .when(bookingRepository.findBookingByIdForUpdate(1L, 1L))
                .thenReturn(Optional.of(booking));
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    bookingService.update(1L, 1L, true);
                }
        );
        assertEquals("Статус изменен ранее на APPROVED", exception.getMessage());
    }

    @Test
    public void update() {
        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(BookingStatus.APPROVED);
        bookingService.update(2L, 1L, true);
        TypedQuery<Booking> query = em.createQuery("Select b from Booking AS b where b.id = :id", Booking.class);
        Booking booking1 = query.setParameter("id",2L).getSingleResult();
        assertThat(booking.getId(), equalTo(booking1.getId()));
        assertThat(booking.getStatus(), equalTo(booking1.getStatus()));
    }

    @Test
    public void updateWithApprovedIsFalse() {
        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStatus(BookingStatus.REJECTED);
        bookingService.update(2L, 1L, false);
        TypedQuery<Booking> query = em.createQuery("Select b from Booking AS b where b.id = :id", Booking.class);
        Booking booking1 = query.setParameter("id",2L).getSingleResult();
        assertThat(booking.getId(), equalTo(booking1.getId()));
        assertThat(booking.getStatus(), equalTo(booking1.getStatus()));
    }

    @Test
    public void whenFindBookingByWrongId_thenCallCustomException() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.findBookingById(5465465465L, 1L);
                }
        );
        assertEquals("Запись о бронировании не найдена", exception.getMessage());
    }

    @Test
    public void whenFindBookingById_thenOk() {
        Booking booking = bookingService.findBookingById(1L, 1L);
        assertThat(booking.getId(), equalTo(1L));
        assertThat(booking.getItem().getId(), equalTo(1L));
        assertThat(booking.getBooker().getId(), equalTo(2L));
    }

    @Test
    public void findBookingByUserIdWithStatusIsALL() {
        List<Booking> list = bookingService.findBookingsByUserId(2L, BookingStatusForFind.ALL, 0, 10);
        assertThat(list.size(), equalTo(4));
    }

    @Test
    public void findBookingByUserIdIsWithStatusALL_resultEmpty() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.findBookingsByUserId(100L, BookingStatusForFind.ALL, 0, 10);
                }
        );
        assertEquals("Записи не найдены", exception.getMessage());
    }

    @Test
    public void findBookingByUserIdWithStatusIsWAITING() {
        List<Booking> list = bookingService.findBookingsByUserId(2L, BookingStatusForFind.WAITING, 0, 10);
        assertThat(list.size(), equalTo(1));
    }

    @Test
    public void findBookingByUserIdWithStatusIsWAITING_resultEmpty() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.findBookingsByUserId(1L, BookingStatusForFind.WAITING, 0, 10);
                }
        );
        assertEquals("Записи не найдены", exception.getMessage());
    }

    @Test
    public void findBookingByUserIdWithStatusIsREJECTED() {
        List<Booking> list = bookingService.findBookingsByUserId(1L, BookingStatusForFind.REJECTED, 0, 10);
        assertThat(list.size(), equalTo(1));
    }

    @Test
    public void findBookingByUserIdWithStatusIsREJECTED_resultEmpty() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.findBookingsByUserId(2L, BookingStatusForFind.REJECTED, 0, 10);
                }
        );
        assertEquals("Записи не найдены", exception.getMessage());
    }

    @Test
    public void findBookingByUserIdWithStatusIsPast() {
        List<Booking> list = bookingService.findBookingsByUserId(2L, BookingStatusForFind.PAST, 0, 10);
        assertThat(list.size(), equalTo(1));
    }

    @Test
    public void findBookingByUserIdWithStatusIsPast_resultEmpty() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.findBookingsByUserId(300L, BookingStatusForFind.PAST, 0, 10);
                }
        );
        assertEquals("Записи не найдены", exception.getMessage());
    }

    @Test
    public void findBookingByUserIdWithStatusIsFuture() {
        List<Booking> list = bookingService.findBookingsByUserId(2L, BookingStatusForFind.FUTURE, 0, 10);
        assertThat(list.size(), equalTo(1));
    }

    @Test
    public void findBookingByUserIdWithStatusIsFuture_resultEmpty() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.findBookingsByUserId(300L, BookingStatusForFind.FUTURE, 0, 10);
                }
        );
        assertEquals("Записи не найдены", exception.getMessage());
    }

    @Test
    public void findBookingByUserIdWithStatusIsCurrent() {
        List<Booking> list = bookingService.findBookingsByUserId(2L, BookingStatusForFind.CURRENT, 0, 10);
        assertThat(list.size(), equalTo(1));
    }

    @Test
    public void findBookingByUserIdWithStatusIsCurrent_resultEmpty() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.findBookingsByUserId(300L, BookingStatusForFind.CURRENT, 0, 10);
                }
        );
        assertEquals("Записи не найдены", exception.getMessage());
    }

    @Test
    public void findBookingByOwnerIdWithStatusALL() {
        List<Booking> list = bookingService.findBookingsByOwner(1L, BookingStatusForFind.ALL, 0, 10);
        assertThat(list.size(), equalTo(4));
    }

    @Test
    public void findBookingByOwnerIdWithStatusALL_resultEmpty() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.findBookingsByOwner(300L, BookingStatusForFind.ALL, 0, 10);
                }
        );
        assertEquals("Записи не найдены", exception.getMessage());
    }

    @Test
    public void findBookingByOwnerIdWithStatusWAITING() {
        List<Booking> list = bookingService.findBookingsByOwner(1L, BookingStatusForFind.WAITING, 0, 10);
        assertThat(list.size(), equalTo(1));
    }

    @Test
    public void findBookingByOwnerIdWithStatusWAITING_resultEmpty() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.findBookingsByOwner(300L, BookingStatusForFind.WAITING, 0, 10);
                }
        );
        assertEquals("Записи не найдены", exception.getMessage());
    }

    @Test
    public void findBookingByOwnerIdWithStatusREJECTED() {
        List<Booking> list = bookingService.findBookingsByOwner(2L, BookingStatusForFind.REJECTED, 0, 10);
        assertThat(list.size(), equalTo(1));
    }

    @Test
    public void findBookingByOwnerIdWithStatusREJECTED_resultEmpty() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.findBookingsByOwner(300L, BookingStatusForFind.REJECTED, 0, 10);
                }
        );
        assertEquals("Записи не найдены", exception.getMessage());
    }

    @Test
    public void findBookingByOwnerIdWithStatusPAST() {
        List<Booking> list = bookingService.findBookingsByOwner(1L, BookingStatusForFind.PAST, 0, 10);
        assertThat(list.size(), equalTo(1));
    }

    @Test
    public void findBookingByOwnerIdWithStatusPAST_resultEmpty() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.findBookingsByOwner(300L, BookingStatusForFind.PAST, 0, 10);
                }
        );
        assertEquals("Записи не найдены", exception.getMessage());
    }

    @Test
    public void findBookingByOwnerIdWithStatusFuture() {
        List<Booking> list = bookingService.findBookingsByOwner(1L, BookingStatusForFind.FUTURE, 0, 10);
        assertThat(list.size(), equalTo(1));
    }

    @Test
    public void findBookingByOwnerIdWithStatusFuture_resultEmpty() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.findBookingsByOwner(300L, BookingStatusForFind.FUTURE, 0, 10);
                }
        );
        assertEquals("Записи не найдены", exception.getMessage());
    }

    @Test
    public void findBookingByOwnerIdWithStatusCurrent() {
        List<Booking> list = bookingService.findBookingsByOwner(1L, BookingStatusForFind.CURRENT, 0, 10);
        assertThat(list.size(), equalTo(1));
    }

    @Test
    public void findBookingByOwnerIdWithStatusCurrent_resultEmpty() {
        Throwable exception = assertThrows(
                ObjNotFoundException.class,
                () -> {
                    bookingService.findBookingsByOwner(300L, BookingStatusForFind.CURRENT, 0, 10);
                }
        );
        assertEquals("Записи не найдены", exception.getMessage());
    }

    @Test
    public void findBookingByOwnerIdWithStatusUNSUPPORTED_resultEmpty() {
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    bookingService.findBookingsByOwner(300L, BookingStatusForFind.UNSUPPORTED_STATUS, 0, 10);
                }
        );
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    public void findBookingByUserIdWithStatusUNSUPPORTED_resultEmpty() {
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    bookingService.findBookingsByUserId(300L, BookingStatusForFind.UNSUPPORTED_STATUS, 0, 10);
                }
        );
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }
}