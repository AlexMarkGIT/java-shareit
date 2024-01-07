package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

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
    public ItemBookingDto getById(@PathVariable Long itemId,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поиск предмета: " + itemId + " пользователем: " + userId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemBookingDto> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поиск предметов пользователя id: " + userId);
        return itemService.getByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String searchText) {
        log.info("Поиск предметов по запросу: " + searchText);
        List<ItemDto> searchedItems = itemService.search(searchText.toLowerCase());
        log.info("Список предметов передан");
        return  searchedItems;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление коммента с текстом: " + commentDto.getText() +
                "\nк вещи: " + itemId + "\n пользователем: " + userId);
        commentDto.setAuthorId(userId);
        commentDto.setItemId(itemId);
        commentDto.setCreated(LocalDateTime.now());
        CommentDto commentDtoCreated = itemService.createComment(commentDto);
        log.info("Комментарий добавлен");
        return  commentDtoCreated;
    }


}
