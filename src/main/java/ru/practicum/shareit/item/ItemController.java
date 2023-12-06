package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление предмета " + itemDto.getName() + " пользователем с id " + userId);
        ItemDto createdItem = itemService.create(itemDto, userId);
        log.info("Предмет добавлен, id:" + createdItem.getId());
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Обновление предмета c id " + itemId + " пользователем с id " + userId);
        itemDto.setId(itemId);
        ItemDto updatedItem = itemService.update(itemDto, userId);
        log.info("Предмет обновлён");
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId) {
        log.info("Запрос предмета с id: " + itemId);
        ItemDto item = itemService.getById(itemId);
        log.info("Предмет отправлен");
        return item;
    }

    @GetMapping
    public List<ItemDto> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос списка предметов пользователя с id: " + userId);
        List<ItemDto> itemsByUser = itemService.getByUserId(userId);
        log.info("Список предметов отправлен");
        return itemsByUser;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String searchText) {
        log.info("Поиск предметов по запросу: " + searchText);
        List<ItemDto> searchedItems = itemService.search(searchText.toLowerCase());
        log.info("Список предметов передан");
        return  searchedItems;
    }
}
