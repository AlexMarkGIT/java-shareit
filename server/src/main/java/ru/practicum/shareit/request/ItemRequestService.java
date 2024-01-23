package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto itemRequestDto, LocalDateTime created, Long userId);

    List<ItemRequestRespDto> getAllByRequester(Long userId);

    List<ItemRequestRespDto> getAll(Long userId, Integer from, Integer size);

    ItemRequestRespDto getById(Long userId, Long requestId);
}
