package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ItemDto itemDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление предмета " + itemDto.getName() + " пользователем с id " + userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemDto itemDto,
                                         @PathVariable Long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Обновление предмета c id " + itemId + " пользователем с id " + userId);
        return itemClient.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поиск предмета: " + itemId + " пользователем: " + userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поиск предметов пользователя id: " + userId);
        return itemClient.getAllItemsByUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(name = "text") String searchText) {
        log.info("Поиск предметов по запросу: " + searchText);
        return itemClient.searchItems(searchText);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody CommentDto commentDto,
                                                @PathVariable Long itemId,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Добавление коммента с текстом: " + commentDto.getText() +
                "\nк вещи: " + itemId + "\n пользователем: " + userId);
        return itemClient.createComment(itemId, commentDto, userId);
    }
}

