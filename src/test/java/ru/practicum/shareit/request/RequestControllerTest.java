package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .description("desc")
            .build();
    private final ItemRequestRespDto itemRequestRespDto = ItemRequestRespDto.builder()
            .id(1L)
            .description("desc")
            .created(LocalDateTime.now())
            .userId(1L)
            .items(List.of())
            .build();

    @Test
    @SneakyThrows
    public void createItemRequest() {
        ItemRequestDto itemRequestDtoForCheck = ItemRequestDto.builder()
                .id(1L)
                .description("desc")
                .userId(1L)
                .build();

        when(itemRequestService.create(any(ItemRequestDto.class)))
                .thenReturn(itemRequestDtoForCheck);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoForCheck.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoForCheck.getDescription())))
                .andExpect(jsonPath("$.userId", is(itemRequestDtoForCheck.getUserId()), Long.class));
    }

    @Test
    @SneakyThrows
    public void getAllByRequester() {
        when(itemRequestService.getAllByRequester(1L)).thenReturn(List.of(itemRequestRespDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestRespDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestRespDto.getDescription())))
                .andExpect(jsonPath("$[0].userId", is(itemRequestRespDto.getUserId()), Long.class));
    }

    @Test
    @SneakyThrows
    public void getAllTest() {
        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestRespDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestRespDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestRespDto.getDescription())))
                .andExpect(jsonPath("$[0].userId", is(itemRequestRespDto.getUserId()), Long.class));
    }

    @Test
    @SneakyThrows
    public void getByIdTest() {
        when(itemRequestService.getById(anyLong(), anyLong())).thenReturn(itemRequestRespDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestRespDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestRespDto.getDescription())))
                .andExpect(jsonPath("$.userId", is(itemRequestRespDto.getUserId()), Long.class));
    }
}
