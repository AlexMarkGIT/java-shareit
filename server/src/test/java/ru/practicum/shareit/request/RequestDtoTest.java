package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jsonDto;
    @Autowired
    private JacksonTester<ItemRequestRespDto> jsonRespDto;

    LocalDateTime testTime = LocalDateTime.now().withNano(0);

    @Test
    @SneakyThrows
    public void itemRequestDtoSerializeTest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("desc")
                .created(testTime)
                .userId(1L)
                .build();

        JsonContent<ItemRequestDto> jsonResult = jsonDto.write(itemRequestDto);

        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.userId").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.created").isEqualTo(testTime.toString());
    }

    @Test
    @SneakyThrows
    public void itemRequestRespDtoSerializeTest() {
        ItemDto itemDto = new ItemDto(1L,
                "itemName",
                "description",
                true,
                1L);

        ItemRequestRespDto itemRequestRespDto = ItemRequestRespDto.builder()
                .id(1L)
                .description("desc")
                .created(testTime)
                .userId(1L)
                .items(List.of(itemDto))
                .build();

        JsonContent<ItemRequestRespDto> jsonResult = jsonRespDto.write(itemRequestRespDto);

        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.userId").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.created").isEqualTo(testTime.toString());
        assertThat(jsonResult)
                .extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(jsonResult)
                .extractingJsonPathStringValue("$.items[0].name").isEqualTo("itemName");
    }
}
