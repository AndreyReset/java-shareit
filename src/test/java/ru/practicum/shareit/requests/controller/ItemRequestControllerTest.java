package ru.practicum.shareit.requests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.dto.ItemRequestCreationDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemRequestController.class)
@TestPropertySource("/application-test.properties")
@ActiveProfiles("test")
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void create() throws Exception {
        ItemRequestCreationDto itemRequestCreationDto = new ItemRequestCreationDto();
        itemRequestCreationDto.setDescription("Описание того, что мне необходимо");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Описание того, что мне необходимо");

        when(itemRequestService.create(any(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemRequestCreationDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    public void findRequestsByOwnerId() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Описание того, что мне необходимо");

        when(itemRequestService.findRequestsByOwnerId(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    public void findRequestsOtherUsersWithPagination() throws Exception {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setId(1L);
        itemRequestDto1.setDescription("Описание того, что мне необходимо");

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(2L);
        itemRequestDto2.setDescription("Описание того, что мне необходимо");

        when(itemRequestService.findRequestsOtherUsersWithPagination(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto1, itemRequestDto2));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    public void findRequestById() throws Exception {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setId(1L);
        itemRequestDto1.setDescription("Описание того, что мне необходимо");

        when(itemRequestService.findRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto1);

        mvc.perform(get("/requests/{requestId}", "1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())));
    }

}