package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("name")
            .email("email@email.com")
            .build();

    @Test
    @SneakyThrows
    public void createUser() {
        UserDto userDtoToCreate = UserDto.builder()
                .name("name")
                .email("email@email.com")
                .build();

        when(userService.create(userDtoToCreate)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    @SneakyThrows
    public void deleteUserFailByFailEmail() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void updateUser() {
        UserDto userDtoToUpdate = UserDto.builder()
                .name("updateName")
                .email("udate@email.com")
                .build();

        UserDto userDtoExpected = UserDto.builder()
                .id(1L)
                .name("updateName")
                .email("udate@email.com")
                .build();

        when(userService.update(any(UserDto.class))).thenReturn(userDtoExpected);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoExpected.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoExpected.getName())))
                .andExpect(jsonPath("$.email", is(userDtoExpected.getEmail())));
    }

    @Test
    @SneakyThrows
    public void getById() {
        when(userService.getById(1L)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    @SneakyThrows
    public void getAll() {
        when(userService.getAll()).thenReturn(List.of(userDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }








}
