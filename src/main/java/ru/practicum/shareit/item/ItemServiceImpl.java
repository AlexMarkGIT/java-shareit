package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryImpl itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        if (!userRepository.contains(userId)) {
            throw new NotFoundException("Владелец предмета не найден с таким id: " + userId);
        }
        Item item = itemMapper.toEntity(itemDto);
        item.setOwnerId(userId);

        return itemMapper.toDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId) {
        if (!itemRepository.contains(itemDto.getId())) {
            throw new NotFoundException("Предмет не найден с таким id: " + itemDto.getId());
        }
        if (!userRepository.contains(userId)) {
            throw new NotFoundException("Владелец предмета не найден с таким id: " + userId);
        }
        if (!itemRepository.isItemWasPostedByUser(itemDto.getId(), userId)) {
            throw new NotFoundException("Владелец предмета не найден с таким id: " + userId);
        }
        Item item = itemMapper.toEntity(itemDto);
        item.setOwnerId(userId);
        return itemMapper.toDto(itemRepository.update(item));
    }

    @Override
    public ItemDto getById(Long itemId) {
        if (!itemRepository.contains(itemId)) {
            throw new NotFoundException("Предмет не найден с таким id: " + itemId);
        }
        return itemMapper.toDto(itemRepository.getById(itemId));
    }

    @Override
    public List<ItemDto> getByUserId(Long userId) {
        if (!userRepository.contains(userId)) {
            throw new NotFoundException("Владелец предмета не найден с таким id: " + userId);
        }
        List<ItemDto> items = new ArrayList<>();
        for (Item item : itemRepository.getByUserId(userId)) {
            items.add(itemMapper.toDto(item));
        }
        return items;
    }

    @Override
    public List<ItemDto> search(String searchText) {
        List<ItemDto> items = new ArrayList<>();
        if (searchText.isBlank()) {
            return items;
        }
        for (Item item : itemRepository.search(searchText)) {
            items.add(itemMapper.toDto(item));
        }
        return items;
    }
}
