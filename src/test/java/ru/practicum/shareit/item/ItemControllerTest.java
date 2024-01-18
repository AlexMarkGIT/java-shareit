package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    private ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("name")
            .description("desc")
            .available(true)
            .build();

    @Test
    @SneakyThrows
    public void createItemTest() {
        ItemDto itemDtoToCreate = ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .build();

        when(itemService.create(itemDtoToCreate, 1L)).thenReturn(itemDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    @SneakyThrows
    public void createItemFailByEmptyNameTest() {
        ItemDto itemDtoFail = ItemDto.builder()
                .name("   ")
                .description("desc")
                .available(true)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDtoFail)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    public void createItemFailByWrongSharerHeaderTest() {
        ItemDto itemDtoToCreate = ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .build();

        when(itemService.create(itemDtoToCreate, 10L))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 10)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    public void updateItemTest() {
        ItemDto itemDtoToUpdate = ItemDto.builder()
                .name("updName")
                .description("updDesc")
                .available(false)
                .build();

        ItemDto itemUpdated = ItemDto.builder()
                .id(1L)
                .name("updName")
                .description("updDesc")
                .available(false)
                .build();

        when(itemService.update(any(ItemDto.class), anyLong())).thenReturn(itemUpdated);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDtoToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemUpdated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemUpdated.getName())))
                .andExpect(jsonPath("$.description", is(itemUpdated.getDescription())))
                .andExpect(jsonPath("$.available", is(itemUpdated.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemUpdated.getRequestId()), Long.class));
    }

    @Test
    @SneakyThrows
    public void getByIdTest() {

        ItemBookingDto itemBookingDto = ItemBookingDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(true)
                .build();

        when(itemService.getById(1L, 1L)).thenReturn(itemBookingDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemBookingDto.getName())))
                .andExpect(jsonPath("$.description", is(itemBookingDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemBookingDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void getByUserIdTest() {
        ItemBookingDto itemBookingDto = ItemBookingDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(true)
                .build();

        when(itemService.getByUserId(1L)).thenReturn(List.of(itemBookingDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemBookingDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemBookingDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemBookingDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void itemSearchTest() {
        when(itemService.search("name")).thenReturn(List.of(itemDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    public void createCommentTest() {
        CommentDto commentDtoToCreate = CommentDto.builder()
                .text("commentText")
                .build();

        CommentDto commentDtoCreated = CommentDto.builder()
                .id(1L)
                .text("commentText")
                .authorName("name")
                .authorId(2L)
                .itemId(1L)
                .build();

        when(itemService.createComment(any(CommentDto.class))).thenReturn(commentDtoCreated);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .content(objectMapper.writeValueAsString(commentDtoToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoCreated.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoCreated.getAuthorName())))
                .andExpect(jsonPath("$.authorId", is(commentDtoCreated.getAuthorId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(commentDtoCreated.getItemId()), Long.class));
    }




}
