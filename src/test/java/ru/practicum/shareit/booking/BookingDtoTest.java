package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingReqDto> jsonReqDto;
    @Autowired
    private JacksonTester<BookingDto> jsonDto;
    private final LocalDateTime start = LocalDateTime.now().withNano(0);
    private final LocalDateTime end = start.plusDays(1);

    @Test
    @SneakyThrows
    public void bookingReqDtoSerializeTest() {
        BookingReqDto bookingReqDto = BookingReqDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        JsonContent<BookingReqDto> jsonResult = jsonReqDto.write(bookingReqDto);

        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
    }

    @Test
    @SneakyThrows
    public void bookingDtoSerializeTest() {
        UserDto userDto = new UserDto(1L, "username", "email@email.com");
        ItemDto itemDto = new ItemDto(1L, "itemName", "description", true, null);
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .item(itemDto)
                .booker(userDto)
                .build();

        JsonContent<BookingDto> jsonResult = jsonDto.write(bookingDto);

        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.status").isEqualTo(Status.WAITING.toString());
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.booker.name").isEqualTo("username");
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.item.name").isEqualTo("itemName");
    }

}
