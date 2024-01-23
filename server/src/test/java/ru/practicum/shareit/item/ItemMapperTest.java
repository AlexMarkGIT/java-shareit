package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {

    private final ItemMapper itemMapper = new ItemMapper();
    private final LocalDateTime testTime = LocalDateTime.now();
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("name")
            .description("desc")
            .available(true)
            .requestId(1L)
            .build();

    private final CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("text")
            .created(testTime)
            .build();

    @Test
    public void toEntityTest() {
        Item item = itemMapper.toEntity(itemDto);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    public void toCommentTest() {
        Comment comment = itemMapper.toComment(commentDto);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }
}
