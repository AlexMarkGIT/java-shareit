package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.exception.BookingByOwnerException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingServiceIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private final User owner = new User(null, "owner","owner@email.com");
    private final User booker = new User(null, "booker","booker@email.com");
    private final Item item = new Item(null, "name", "desc",
            true, owner, null);

    private final LocalDateTime testTime = LocalDateTime.now();

    private final BookingReqDto booking1 = BookingReqDto.builder()
            .start(testTime.plusDays(1))
            .end(testTime.plusDays(2))
            .itemId(1L)
            .build();

    private final BookingReqDto booking2 = BookingReqDto.builder()
            .start(testTime.plusDays(3))
            .end(testTime.plusDays(4))
            .itemId(1L)
            .build();

    private final BookingReqDto booking3 = BookingReqDto.builder()
            .start(testTime.plusDays(5))
            .end(testTime.plusDays(6))
            .itemId(1L)
            .build();

    @Test
    @DirtiesContext
    public void bookingServiceIntegrationTest() {
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        UserDto ownerDto = userService.getById(1L);
        UserDto bookerDto = userService.getById(2L);
        ItemBookingDto itemDto = itemService.getById(1L, 1L);

        bookingService.create(booking1, bookerDto.getId());
        bookingService.create(booking2, bookerDto.getId());
        bookingService.create(booking3, bookerDto.getId());

        List<BookingDto> bookingDtoList = bookingService
                .getAllByBooker(2L, State.ALL, 0, 10);

        assertEquals(3, bookingDtoList.size());

        bookingService.update(2L, ownerDto.getId(), true);
        bookingService.update(3L, ownerDto.getId(), false);

        BookingDto bookingDto1 = bookingService.getById(1L, 1L);
        BookingDto bookingDto2 = bookingService.getById(2L, 1L);
        BookingDto bookingDto3 = bookingService.getById(3L, 1L);

        assertTrue(bookingDtoList.contains(bookingDto1));
        assertTrue(bookingDtoList.contains(bookingDto2));
        assertTrue(bookingDtoList.contains(bookingDto3));

        assertEquals(Status.WAITING, bookingDto1.getStatus());
        assertEquals(Status.APPROVED, bookingDto2.getStatus());
        assertEquals(Status.REJECTED, bookingDto3.getStatus());

        assertEquals(itemDto.getId(), bookingDto1.getItem().getId());
        assertEquals(itemDto.getId(), bookingDto2.getItem().getId());
        assertEquals(itemDto.getId(), bookingDto3.getItem().getId());

        assertEquals(1, bookingService.getAllByBooker(2L, State.WAITING, 0, 10)
                .size());
        assertTrue(bookingService.getAllByBooker(2L, State.WAITING, 0, 10)
                .contains(bookingDto1));
        assertEquals(1, bookingService.getAllByBooker(2L, State.REJECTED, 0, 10)
                .size());
        assertTrue(bookingService.getAllByOwner(1L, State.REJECTED, 0, 10)
                .contains(bookingDto3));
        assertEquals(1, bookingService.getAllByOwner(1L, State.WAITING, 0, 10)
                .size());
        assertTrue(bookingService.getAllByOwner(1L, State.WAITING, 0, 10)
                .contains(bookingDto1));
        assertEquals(1, bookingService.getAllByOwner(1L, State.REJECTED, 0, 10)
                .size());
        assertTrue(bookingService.getAllByOwner(1L, State.REJECTED, 0, 10)
                .contains(bookingDto3));

        assertThrows(BookingByOwnerException.class,
                () -> bookingService.create(booking1, 1L));
        assertThrows(NotFoundException.class,
                () -> bookingService.update(1000L, 1L, true));
        assertThrows(NotFoundException.class,
                () -> bookingService.update(1L, 1000L, true));
        assertThrows(BookingException.class,
                () -> bookingService.update(2L, 1L, true));
        assertThrows(BookingByOwnerException.class,
                () -> bookingService.update(1L, 2L, true));
    }



}
