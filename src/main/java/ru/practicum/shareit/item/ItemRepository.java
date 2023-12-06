package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item create(Item item);
    Item update(Item item);
    Item getById(Long id);
    List<Item> getByUserId(Long userId);
    List<Item> search(String searchText);
    Boolean contains(Long itemId);
    Boolean isItemWasPostedByUser(Long itemId, Long userId);
}
