package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class RequestServiceIntegrationTest {
    @Autowired
    private ItemRequestServiceImpl itemRequestService;
    @Autowired
    private UserRepository userRepository;

    private final LocalDateTime testTime = LocalDateTime.now().withNano(0);
    private final User requester = new User(1L, "requester","requester@email.com");
    private final User randomUser = new User(2L, "randomUser","randomUser");

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .description("desc")
            .created(testTime)
            .userId(1L)
            .build();
    private final ItemRequestRespDto itemRequestRespDto = ItemRequestRespDto.builder()
            .id(1L)
            .description("desc")
            .created(testTime)
            .userId(1L)
            .items(List.of())
            .build();

    @Test
    @DirtiesContext
    public void itemRequestServiceIntegrationTest() {
        userRepository.save(requester);
        userRepository.save(randomUser);
        itemRequestService.create(itemRequestDto, testTime, 1L);

        ItemRequestRespDto itemDtoFromService = itemRequestService.getById(1L, 1L);
        List<ItemRequestRespDto> itemDtoListAll = itemRequestService.getAll(2L, 0, 10);
        List<ItemRequestRespDto> itemDtoListAllEmpty = itemRequestService.getAll(1L, 0, 10);
        List<ItemRequestRespDto> itemDtoListOfRequester
                = itemRequestService.getAllByRequester(1L);

        assertEquals(itemDtoFromService, itemRequestRespDto);
        assertEquals(1, itemDtoListAll.size());
        assertEquals(1, itemDtoListOfRequester.size());
        assertTrue(itemDtoListAll.contains(itemRequestRespDto));
        assertTrue(itemDtoListOfRequester.contains(itemRequestRespDto));

        assertEquals(0, itemDtoListAllEmpty.size());
    }
}
