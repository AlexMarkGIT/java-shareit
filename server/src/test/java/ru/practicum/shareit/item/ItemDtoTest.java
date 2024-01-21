package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> jsonItemDto;
    @Autowired
    private JacksonTester<ItemBookingDto> jsonItemBookingDto;
    @Autowired
    private JacksonTester<CommentDto> jsonCommentDto;
    //private final LocalDateTime testTime = LocalDateTime.now().withSecond(0).withNano(0);

    private final LocalDateTime testTime = LocalDateTime.of(2024,1,1,0,0,0);

    @Test
    @SneakyThrows
    public void itemDtoSerializeTest() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemDto> jsonResult = jsonItemDto.write(itemDto);

        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(jsonResult)
                .extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    @SneakyThrows
    public void itemBookingDtoSerializeTest() {

        BookingItemDto bookingItemDtoLast = BookingItemDto.builder()
                .id(1L)
                .start(testTime)
                .end(testTime)
                .status(Status.WAITING)
                .bookerId(1L)
                .itemId(1L)
                .build();

        BookingItemDto bookingItemDtoNext = BookingItemDto.builder()
                .id(2L)
                .start(testTime)
                .end(testTime)
                .status(Status.WAITING)
                .bookerId(1L)
                .itemId(1L)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .itemId(1L)
                .created(testTime)
                .authorName("name")
                .authorId(1L)
                .build();

        ItemBookingDto itemBookingDto = ItemBookingDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(true)
                .lastBooking(bookingItemDtoLast)
                .nextBooking(bookingItemDtoNext)
                .comments(List.of(commentDto))
                .build();

        JsonContent<ItemBookingDto> jsonResult = jsonItemBookingDto.write(itemBookingDto);

        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(jsonResult)
                .extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.lastBooking.itemId").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.nextBooking.itemId").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
    }

    @Test
    @SneakyThrows
    public void commentDtoSerializeTest() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .itemId(1L)
                .created(testTime)
                .authorName("name")
                .authorId(1L)
                .build();

        JsonContent<CommentDto> jsonResult = jsonCommentDto.write(commentDto);

        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.created").isEqualTo("2024-01-01T00:00:00");
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.authorName").isEqualTo("name");
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.authorId").isEqualTo(1);
    }


}
