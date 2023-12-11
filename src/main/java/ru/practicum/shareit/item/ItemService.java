package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long userId);

    ItemDto getById(Long itemId);

    List<ItemDto> getByUserId(Long userId);

    List<ItemDto> search(String searchText);
}
