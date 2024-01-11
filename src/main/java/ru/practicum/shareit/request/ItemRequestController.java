package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    ItemRequestDto create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {

        log.info("Создание запроса пользователем с id: " + userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setUserId(userId);
        ItemRequestDto itemRequestDtoResp = itemRequestService.create(itemRequestDto);
        log.info("Запрос создан с id: " + itemRequestDtoResp.getId());
        return itemRequestDtoResp;
    }

    @GetMapping //тут только для просителя
    List<ItemRequestRespDto> getAllByRequester(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllByRequester(userId);
    }

    @GetMapping("/all") //тут все запросы кроме своих от более новых к более старым
    List<ItemRequestRespDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam(name = "from", defaultValue = "0") @Min(0)  Integer from,
                                @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}") //просматривать может любой
    ItemRequestRespDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long requestId) {
        return itemRequestService.getById(userId, requestId);
    }

}
