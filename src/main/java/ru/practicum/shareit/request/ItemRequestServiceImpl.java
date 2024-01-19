package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final ItemRequestMapper mapper;

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, LocalDateTime created, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));

        itemRequestDto.setCreated(created);
        itemRequestDto.setUserId(userId);
        ItemRequest itemRequest = mapper.toEntity(itemRequestDto);
        itemRequest.setUser(user);

        return mapper.toDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestRespDto> getAllByRequester(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));

        List<ItemRequestRespDto> requests = new ArrayList<>();
        List<ItemRequest> requestsByUser =
                itemRequestRepository.findAllByUserIdOrderByCreatedDesc(user.getId());

        if (!requestsByUser.isEmpty()) {
            for (ItemRequest request : requestsByUser) {
                ItemRequestRespDto itemRequestRespDto = mapper.toRespDto(request);
                itemRequestRespDto.setItems(itemService.getByRequest(request.getId()));
                requests.add(itemRequestRespDto);
            }
        }

        return requests;
    }

    @Override
    public List<ItemRequestRespDto> getAll(Long userId, Integer from, Integer size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));

        List<ItemRequestRespDto> requests = new ArrayList<>();
        List<ItemRequest> requestsByUser =
                itemRequestRepository.findAllByUserIdNotOrderByCreatedDesc(user.getId(),
                        PageRequest.of(from / size, size));

        if (!requestsByUser.isEmpty()) {
            for (ItemRequest request : requestsByUser) {
                ItemRequestRespDto itemRequestRespDto = mapper.toRespDto(request);
                itemRequestRespDto.setItems(itemService.getByRequest(request.getId()));
                requests.add(itemRequestRespDto);
            }
        }

        return requests;
    }

    @Override
    public ItemRequestRespDto getById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден с id: " + requestId));

        ItemRequestRespDto itemRequestRespDto = mapper.toRespDto(itemRequest);
        itemRequestRespDto.setItems(itemService.getByRequest(requestId));

        return itemRequestRespDto;
    }
}
