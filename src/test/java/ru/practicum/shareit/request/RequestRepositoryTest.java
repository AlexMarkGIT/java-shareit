package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestRepositoryTest {
    @Autowired
    private TestEntityManager testEM;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private final LocalDateTime testTime = LocalDateTime.now();

    private final User owner = new User(null, "owner","owner@email.com");
    private final User requester = new User(null, "booker","booker@email.com");
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("desc")
            .created(testTime)
            .user(requester)
            .build();

    private final Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    public void persistData() {
        testEM.persist(owner);
        testEM.persist(requester);
        testEM.flush();

        itemRequestRepository.save(itemRequest);
    }

    @Test
    public void findByIdTest() {
        ItemRequest itemRequestFromRepo = itemRequestRepository.findById(1L).orElseThrow();

        assertEquals(itemRequest, itemRequestFromRepo);
    }

    @Test
    public void findAllByRequesterTest() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByUserIdOrderByCreatedDesc(2L);

        assertEquals(1, itemRequests.size());
        assertTrue(itemRequests.contains(itemRequest));
    }

    @Test
    public void findAllNotByRequesterTest() {
        List<ItemRequest> itemRequests =
                itemRequestRepository.findAllByUserIdNotOrderByCreatedDesc(1L, pageable);

        assertEquals(1, itemRequests.size());
        assertTrue(itemRequests.contains(itemRequest));
    }
}
