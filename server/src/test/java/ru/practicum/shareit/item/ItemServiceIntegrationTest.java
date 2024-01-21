package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private final User owner = new User(1L, "owner","owner@email.com");
    private final User requester = new User(2L, "requester","requester@email.com");
    private final User booker = new User(3L, "booker","booker@email.com");
    private final ItemRequest request =
            new ItemRequest(1L, "desc", LocalDateTime.now(), requester);
    private final Item item = new Item(null, "name", "desc",
            true, owner, request);
    private final Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now())
            .item(item)
            .booker(booker)
            .status(Status.APPROVED)
            .build();
    private final Comment comment = Comment.builder()
            .id(1L)
            .text("text")
            .created(LocalDateTime.now())
            .author(booker)
            .item(item)
            .build();

    @Test
    @DirtiesContext
    public void itemServiceIntegrationTest() {
        userRepository.save(owner);
        userRepository.save(requester);
        userRepository.save(booker);
        itemRequestRepository.save(request);
        itemRepository.save(item);
        bookingRepository.save(booking);
        commentRepository.save(comment);

        ItemDto itemDtoUpdated = ItemDto.builder()
                .id(1L)
                .name("updatedName")
                .build();

        assertEquals(1L, itemService.getById(1L, 1L).getId());
        assertEquals(3L,
                itemService.getById(1L, 1L).getLastBooking().getBookerId());
        assertEquals(1L, itemService.getByUserId(1L).get(0).getId());
        assertEquals("updatedName", itemService.update(itemDtoUpdated, 1L).getName());
        assertEquals(1L, itemService.getByRequest(request.getId()).get(0).getId());
        assertEquals(1L, itemService.search("updated").get(0).getId());
        assertEquals(1L, itemService.getByRequest(1L).get(0).getId());
    }
}
