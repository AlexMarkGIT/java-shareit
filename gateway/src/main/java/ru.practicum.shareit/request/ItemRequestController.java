package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@Validated
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    ResponseEntity<Object> create(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {

        log.info("Создание запроса пользователем с id: " + userId);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    ResponseEntity<Object> getAllByRequester(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getAllByRequester(userId);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(name = "from", defaultValue = "0") @Min(value = 0)Integer from,
                                    @RequestParam(name = "size", defaultValue = "10") @Min(value = 1) Integer size) {
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long requestId) {
        return itemRequestClient.getById(userId, requestId);
    }
}
