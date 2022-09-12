package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreationDtoIn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@TestPropertySource("/application-test.properties")
@ActiveProfiles("test")
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void createBooking() throws Exception {
        BookingCreationDtoIn bookingCreationDtoIn = createDtoIn(1L);
        Booking booking = create(bookingCreationDtoIn);


        when(bookingService.create(any(), anyLong()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(bookingCreationDtoIn))
                .header("X-Sharer-User-Id", "1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class));
    }

    @Test
    public void updateBooking() throws Exception {
        BookingCreationDtoIn bookingCreationDtoIn = createDtoIn(1L);
        Booking booking = create(bookingCreationDtoIn);

        when(bookingService.update(1L, 1L, true))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/{bookingId}", "1")
                .content(mapper.writeValueAsString(booking))
                .header("X-Sharer-User-Id", "1")
                .param("approved", "true")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class));
    }

    @Test
    public void findBookingById() throws Exception {
        BookingCreationDtoIn bookingCreationDtoIn = createDtoIn(1L);
        Booking booking = create(bookingCreationDtoIn);

        when(bookingService.findBookingById(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", "1")
                .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class));
    }

    @Test
    public void findBookingsByUserId() throws Exception {
        BookingCreationDtoIn bookingCreationDtoIn = createDtoIn(1L);
        Booking booking = create(bookingCreationDtoIn);

        when(bookingService.findBookingsByUserId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    public void findBookingsByOwner() throws Exception {
        BookingCreationDtoIn bookingCreationDtoIn = createDtoIn(1L);
        Booking booking = create(bookingCreationDtoIn);

        when(bookingService.findBookingsByOwner(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    private Booking create(BookingCreationDtoIn b) {
        Booking booking = new Booking();
        booking.setId(b.getItemId());
        booking.setStart(LocalDateTime.now().plusSeconds(b.getItemId()));
        booking.setEnd(LocalDateTime.now().plusHours(b.getItemId()));
        Item item = new Item();
        item.setId(b.getItemId());
        booking.setItem(item);
        booking.setBooker(new User());
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    private BookingCreationDtoIn createDtoIn(long id) {
        BookingCreationDtoIn booking = new BookingCreationDtoIn();
        booking.setStart(LocalDateTime.now().plusSeconds(id));
        booking.setEnd(LocalDateTime.now().plusHours(id));
        booking.setItemId(id);
        return booking;
    }
}