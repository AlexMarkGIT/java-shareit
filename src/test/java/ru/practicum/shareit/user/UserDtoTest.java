package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> jsonDto;

    @Test
    @SneakyThrows
    public void userDtoSerializeTest() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@email")
                .build();

        JsonContent<UserDto> jsonResult = jsonDto.write(userDto);

        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }
}
