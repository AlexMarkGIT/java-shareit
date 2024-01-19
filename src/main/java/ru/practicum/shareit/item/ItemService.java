package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long userId);

    ItemBookingDto getById(Long itemId, Long userId);

    List<ItemBookingDto> getByUserId(Long userId);

    List<ItemDto> search(String searchText);

    CommentDto createComment(CommentDto comment);

    List<ItemDto> getByRequest(Long requestId);
}
