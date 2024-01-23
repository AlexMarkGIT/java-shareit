package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    private final UserDto userDto1 = UserDto.builder()
            .id(1L)
            .name("name")
            .email("email@email.com")
            .build();

    private final UserDto userDto2 = UserDto.builder()
            .name("name2")
            .email("email2@email.com")
            .build();

    private final UserDto userDto3 = UserDto.builder()
            .name("name3")
            .email("email3@email.com")
            .build();

    @Test
    @DirtiesContext
    public void userServiceIntegrationTest() {
        UserDto userDto1Resp = userService.create(userDto1);
        UserDto userDto2Resp = userService.create(userDto2);
        UserDto userDto3Resp = userService.create(userDto3);

        assertEquals(1L, userDto1Resp.getId());
        assertEquals(2L, userDto2Resp.getId());
        assertEquals(3L, userDto3Resp.getId());

        UserDto userDto3ToUpd =  UserDto.builder()
                .id(3L)
                .name("name3upd")
                .email("email3upd@email.com")
                .build();

        UserDto userDto3Updated = userService.update(userDto3ToUpd);

        assertEquals(3L, userDto3Updated.getId());
        assertEquals("name3upd", userDto3Updated.getName());
        assertEquals("email3upd@email.com", userDto3Updated.getEmail());

        UserDto userDtoRandomToUpd = UserDto.builder()
                .id(333L)
                .name("name333upd")
                .email("email333upd@email.com")
                .build();

        assertThrows(NotFoundException.class, () -> userService.update(userDtoRandomToUpd));

        assertEquals(userDto1Resp, userService.getById(1L));
        assertEquals(userDto2Resp, userService.getById(2L));
        assertEquals(userDto3Updated, userService.getById(3L));

        assertThrows(NotFoundException.class, () -> userService.getById(333L));

        List<UserDto> allUsers = userService.getAll();
        assertEquals(3, allUsers.size());
        assertTrue(allUsers.contains(userDto1Resp));
        assertTrue(allUsers.contains(userDto2Resp));
        assertTrue(allUsers.contains(userDto3Updated));
    }
}
