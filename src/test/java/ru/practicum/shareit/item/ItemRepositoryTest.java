package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager testEM;
    @Autowired
    private ItemRepository itemRepository;

    private final User owner = new User(null, "owner","owner@email.com");
    private final User requester = new User(null, "requester","requester@email.com");
    private final ItemRequest request =
            new ItemRequest(null, "desc", LocalDateTime.now(), requester);

    private final Item item = new Item(null, "name", "desc",
            true, owner, request);
    private final Item itemToCheck = new Item(1L, "name", "desc",
            true, owner, request);

    @BeforeEach
    public void persistData() {
        testEM.persist(owner);
        testEM.persist(requester);
        testEM.persist(request);
        testEM.flush();

        itemRepository.save(item);
    }

    @Test
    public void getByIdTest() {
        Item itemFromRepo = itemRepository.findById(1L).orElseThrow();

        assertEquals(1L, itemFromRepo.getId());
        assertEquals("name", itemFromRepo.getName());
        assertEquals(1L, itemFromRepo.getOwner().getId());
        assertEquals(1L, itemFromRepo.getRequest().getId());
        assertEquals(2L, itemFromRepo.getRequest().getUser().getId());
    }

    @Test
    public void getAllTest() {
        List<Item> itemsAll = itemRepository.findAll();

        assertEquals(1, itemsAll.size());
        assertTrue(itemsAll.contains(itemToCheck));
    }

    @Test
    public void getAllByOwnerIdTest() {
        List<Item> itemsByOwner = itemRepository.findAllByOwnerId(1L);

        assertEquals(1, itemsByOwner.size());
        assertTrue(itemsByOwner.contains(itemToCheck));
    }

    @Test
    public void getAllByRequestId() {
        List<Item> itemsByRequest = itemRepository.findAllByRequestId(1L);

        assertEquals(1, itemsByRequest.size());
        assertTrue(itemsByRequest.contains(itemToCheck));
    }

    @Test
    public void searchTest() {
        List<Item> itemsBySearch = itemRepository.search("name");

        assertEquals(1, itemsBySearch.size());
        assertTrue(itemsBySearch.contains(itemToCheck));
    }



}
