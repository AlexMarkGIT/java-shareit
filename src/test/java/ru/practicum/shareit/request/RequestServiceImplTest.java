package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private ItemRequestMapper mapper;

    private final LocalDateTime testTime = LocalDateTime.now();
    private final User requester = new User(2L, "requester","requester@email.com");
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("desc")
            .created(testTime)
            .user(requester)
            .build();

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("desc")
            .created(LocalDateTime.now())
            .userId(2L)
            .build();
    private final ItemRequestRespDto itemRequestRespDto = ItemRequestRespDto.builder()
            .id(1L)
            .description("desc")
            .created(LocalDateTime.now())
            .userId(2L)
            .items(List.of())
            .build();

    @Test
    public void createItemRequestTest() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(mapper.toEntity(any(ItemRequestDto.class))).thenReturn(itemRequest);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(mapper.toDto(any(ItemRequest.class))).thenReturn(itemRequestDto);

        assertEquals(itemRequestDto, itemRequestService.create(itemRequestDto));
    }

    @Test
    public void createItemRequestFailByUnknownUserTest() {
        when(userRepository.findById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemRequestService.create(itemRequestDto));
    }

    @Test
    public void getByIdTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(mapper.toRespDto(itemRequest)).thenReturn(itemRequestRespDto);
        when(itemService.getByRequest(anyLong())).thenReturn(List.of());

        assertEquals(itemRequestRespDto, itemRequestService.getById(1L, 2L));
    }

    @Test
    public void getAllTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findAllByUserIdNotOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(List.of(itemRequest));
        when(mapper.toRespDto(itemRequest)).thenReturn(itemRequestRespDto);
        when(itemService.getByRequest(anyLong())).thenReturn(List.of());

        List<ItemRequestRespDto> requestRespDtoList = itemRequestService.getAll(1L, 0, 10);

        assertEquals(1, requestRespDtoList.size());
        assertTrue(requestRespDtoList.contains(itemRequestRespDto));
    }

    @Test
    public void getAllByRequester() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findAllByUserIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(itemRequest));
        when(mapper.toRespDto(itemRequest)).thenReturn(itemRequestRespDto);
        when(itemService.getByRequest(anyLong())).thenReturn(List.of());

        List<ItemRequestRespDto> requestRespDtoList = itemRequestService.getAllByRequester(1L);

        assertEquals(1, requestRespDtoList.size());
        assertTrue(requestRespDtoList.contains(itemRequestRespDto));
    }
}
