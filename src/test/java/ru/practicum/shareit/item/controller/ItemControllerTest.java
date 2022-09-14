package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@TestPropertySource("/application-test.properties")
@ActiveProfiles("test")
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void findItemsByUserId() throws Exception {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Отвёртка");
        item1.setDescription("Крестовая надежная");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Молоток");
        item2.setDescription("Стальной тяжёлый");
        item2.setAvailable(true);

        when(itemService.findItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(item1, item2));

        mvc.perform(get("/items"))
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    public void findItemById() throws Exception {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Отвёртка");
        item1.setDescription("Крестовая надежная");
        item1.setAvailable(true);

        when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(item1);

        mvc.perform(get("/items/{id}", "1"))
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())));
    }

    @Test
    public void createItem() throws Exception {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Отвёртка");
        item1.setDescription("Крестовая надежная");
        item1.setAvailable(true);

        when(itemService.create(any(), anyLong()))
                .thenReturn(item1);

        mvc.perform(post("/items")
                .content(mapper.writeValueAsString(item1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())));
    }

    @Test
    public void updateItem() throws Exception {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Отвёртка");
        item1.setDescription("Крестовая надежная");
        item1.setAvailable(true);

        when(itemService.update(any(), anyLong(), anyLong()))
                .thenReturn(item1);

        mvc.perform(patch("/items/{id}", "1")
                .content(mapper.writeValueAsString(item1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())))
                .andExpect(jsonPath("$.description", is(item1.getDescription())));
    }

    @Test
    public void search() throws Exception {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Отвёртка");
        item1.setDescription("Крестовая надежная");
        item1.setAvailable(true);

        when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(item1));

        mvc.perform(get("/items/search")
                .param("text", "Отвёртка"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    public void addComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Комментарий");

        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", "1")
                .content(mapper.writeValueAsString(commentDto))
                .requestAttr("X-Sharer-User-Id", "1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()),Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));

    }

    @Test
    public void whenCreateWithNameIsEmpty_thenException() throws Exception {
        ItemCreationDto item = new ItemCreationDto();
        item.setDescription("Описание");
        item.setAvailable(true);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Название вещи не может быть пустым")));
    }

    @Test
    public void whenCreateWithDescriptionIsEmpty_thenException() throws Exception {
        ItemCreationDto item = new ItemCreationDto();
        item.setName("Описание");
        item.setAvailable(true);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Описание вещи не может быть пустым")));
    }

    @Test
    public void whenCreateWithAvailableIsnull_thenException() throws Exception {
        ItemCreationDto item = new ItemCreationDto();
        item.setName("Описание");
        item.setDescription("desc");

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("Изначально вещь должна быть доступна")));
    }

}