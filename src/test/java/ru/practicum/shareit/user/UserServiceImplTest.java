package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository repository;
    @Mock
    private UserMapper mapper;

    private final User userToCreate = User.builder()
            .name("name")
            .email("email@email.com")
            .build();

    private final User user = User.builder()
            .id(1L)
            .name("name")
            .email("email@email.com")
            .build();

    private final User userUpd = User.builder()
            .id(1L)
            .name("upd")
            .email("upd@email.com")
            .build();

    private final UserDto userDtoToCreate = UserDto.builder()
            .name("name")
            .email("email@email.com")
            .build();

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("name")
            .email("email@email.com")
            .build();

    private final UserDto userDtoToUpd = UserDto.builder()
            .id(1L)
            .name("upd")
            .email("upd@email.com")
            .build();


    @Test
    public void createUserTest() {
        when(mapper.toEntity(userDtoToCreate)).thenReturn(userToCreate);
        when(repository.save(userToCreate)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(userDto);

        assertEquals(userDto, userService.create(userDtoToCreate));
    }

    @Test
    public void updateUserTest() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(userUpd)).thenReturn(userUpd);
        when(mapper.toDto(userUpd)).thenReturn(userDtoToUpd);

        assertEquals(userDtoToUpd, userService.update(userDtoToUpd));
    }

    @Test
    public void getByIdTest() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(userDto);

        assertEquals(userDto, userService.getById(1L));
    }

    @Test
    public void getByIdNotFoundTest() {
        when(repository.findById(10L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.getById(10L));
    }

    @Test
    public void getAllTest() {
        when(repository.findAll()).thenReturn(List.of(user));
        when(mapper.toDto(user)).thenReturn(userDto);

        assertEquals(List.of(userDto), userService.getAll());
    }
}
