package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager testEM;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private final User owner = new User(null, "owner","owner@email.com");
    private final User booker = new User(null, "booker","booker@email.com");
    private final Item item = new Item(null, "name", "desc",
            true, owner, null);
    private final LocalDateTime testTime = LocalDateTime.now();
    private final Booking bookingWaitingFuture = Booking.builder()
            .start(testTime.plusDays(2))
            .end(testTime.plusDays(3))
            .item(item)
            .booker(booker)
            .status(Status.WAITING)
            .build();

    private final Booking bookingApprovedCurrent = Booking.builder()
            .start(testTime.minusDays(1))
            .end(testTime.plusDays(1))
            .item(item)
            .booker(booker)
            .status(Status.APPROVED)
            .build();

    private final Booking bookingRejectedPast = Booking.builder()
            .start(testTime.minusDays(3))
            .end(testTime.minusDays(2))
            .item(item)
            .booker(booker)
            .status(Status.REJECTED)
            .build();

    private final Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    public void persistData() {
        testEM.persist(owner);
        testEM.persist(booker);
        testEM.persist(item);
        testEM.flush();

        bookingRepository.save(bookingRejectedPast);
        bookingRepository.save(bookingApprovedCurrent);
        bookingRepository.save(bookingWaitingFuture);
    }

    @Test
    public void getByIdTest() {
        Booking booking1 = bookingRepository.findById(1L).orElseThrow();
        Booking booking2 = bookingRepository.findById(2L).orElseThrow();
        Booking booking3 = bookingRepository.findById(3L).orElseThrow();

        assertEquals(1L, booking1.getId());
        assertEquals(2L, booking2.getId());
        assertEquals(3L, booking3.getId());
    }

    @Test
    public void getAllByBookerTest() {
        List<Booking> bookings = bookingRepository.findAllByBooker(2L, pageable);

        assertEquals(3, bookings.size());
    }

    @Test
    public void getAllCurrentByBookerTest() {
        List<Booking> bookings = bookingRepository.findAllCurrentByBooker(2L, testTime, pageable);

        assertEquals(1, bookings.size());
        assertEquals(2L, bookings.get(0).getId());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    public void getAllPastByBookerTest() {
        List<Booking> bookings = bookingRepository.findAllPastByBooker(2L, testTime, pageable);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    public void getAllFutureByBookerTest() {
        List<Booking> bookings = bookingRepository.findAllFutureByBooker(2L, testTime, pageable);

        assertEquals(1, bookings.size());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    public void getAllWaitingByBookerTest() {
        List<Booking> bookings = bookingRepository.findAllWaitingByBooker(2L, testTime, pageable);

        assertEquals(1, bookings.size());
        assertEquals(3L, bookings.get(0).getId());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
    }

    @Test
    public void getAllRejectedByBookerTest() {
        List<Booking> bookings = bookingRepository.findAllRejectedByBooker(2L, pageable);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
        assertEquals(Status.REJECTED, bookings.get(0).getStatus());
    }

    @Test
    public void getAllByOwnerTest() {
        List<Booking> bookings = bookingRepository.findAllByOwner(1L, pageable);

        assertEquals(3, bookings.size());
    }

    @Test
    public void getAllCurrentByOwnerTest() {
        List<Booking> bookings = bookingRepository.findAllCurrentByOwner(1L, testTime, pageable);

        assertEquals(1, bookings.size());
        assertEquals(2L, bookings.get(0).getId());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    public void getAllPastByOwnerTest() {
        List<Booking> bookings = bookingRepository.findAllPastByOwner(1L, testTime, pageable);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    public void getAllFutureByOwnerTest() {
        List<Booking> bookings = bookingRepository.findAllFutureByOwner(1L, testTime, pageable);

        assertEquals(1, bookings.size());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    public void getAllWaitingByOwnerTest() {
        List<Booking> bookings = bookingRepository.findAllWaitingByOwner(1L, testTime, pageable);

        assertEquals(1, bookings.size());
        assertEquals(3L, bookings.get(0).getId());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
    }

    @Test
    public void getAllRejectedByOwnerTest() {
        List<Booking> bookings = bookingRepository.findAllRejectedByOwner(1L, pageable);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
        assertEquals(Status.REJECTED, bookings.get(0).getStatus());
    }









}
