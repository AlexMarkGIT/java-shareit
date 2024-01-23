package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;

@Component
public class ItemRequestMapper {
    public ItemRequest toEntity(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .build();
    }

    public ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .userId(itemRequest.getUser().getId())
                .build();
    }

    public ItemRequestRespDto toRespDto(ItemRequest itemRequest) {
        return ItemRequestRespDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .userId(itemRequest.getUser().getId())
                .build();
    }
}
