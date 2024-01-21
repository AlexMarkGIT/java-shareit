package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.exception.BookingByOwnerException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    private final UserMapper userMapper = new UserMapper();
    private final ItemMapper itemMapper = new ItemMapper();


    private final User owner = new User(1L, "owner","owner@email.com");
    private final User booker = new User(2L, "booker","booker@email.com");
    private final Item item = new Item(1L, "name", "desc",
            true, owner, null);

    private final LocalDateTime testTime = LocalDateTime.now();

    private final BookingReqDto bookingReqDtoValid = BookingReqDto.builder()
            .start(testTime.plusDays(1))
            .end(testTime.plusDays(2))
            .itemId(1L)
            .build();

    private final Booking validBooking = Booking.builder()
            .id(1L)
            .start(testTime.plusDays(1))
            .end(testTime.plusDays(2))
            .status(Status.WAITING)
            .booker(booker)
            .item(item)
            .build();

    @Test
    public void createBookingValidTest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Booking bookingFromMapper = Booking.builder()
                .start(testTime.plusDays(1))
                .end(testTime.plusDays(2))
                .build();

        when(bookingMapper.bookingReqDtoToBooking(bookingReqDtoValid))
                .thenReturn(bookingFromMapper);

        bookingFromMapper.setStatus(Status.WAITING);
        bookingFromMapper.setBooker(booker);
        bookingFromMapper.setItem(item);

        when(bookingRepository.save(bookingFromMapper))
                .thenReturn(validBooking);

        BookingDto bookingDtoExpected = BookingDto.builder()
                .id(1L)
                .start(testTime.plusDays(1))
                .end(testTime.plusDays(2))
                .status(Status.WAITING)
                .booker(userMapper.toDto(booker))
                .item(itemMapper.toDto(item))
                .build();

        when(bookingMapper.bookingToDto(validBooking)).thenReturn(bookingDtoExpected);

        BookingDto bookingDtoActual = bookingService.create(bookingReqDtoValid, 2L);

        assertEquals(bookingDtoExpected, bookingDtoActual);
    }

    @Test
    public void createBookingNotValidByUserTest() {
        when(userRepository.findById(10L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingReqDtoValid, 10L));
    }

    @Test
    public void createBookingNotValidByItemTest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenThrow(NotFoundException.class);

        BookingReqDto bookingReqDto = BookingReqDto.builder()
                .itemId(10L)
                .start(testTime)
                .end(testTime)
                .build();

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingReqDto, 2L));
    }

    @Test
    public void createBookingNotValidByNotAvailableItemTest() {
        Item itemNotAvailable;
        itemNotAvailable = item;
        itemNotAvailable.setAvailable(false);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemNotAvailable));

        assertThrows(ItemNotAvailableException.class,
                () -> bookingService.create(bookingReqDtoValid, 2L));
    }

    @Test
    public void updateBookingApprovedByOwnerTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(validBooking));

        Booking bookingWithUpdateStatus = Booking.builder()
                .id(validBooking.getId())
                .item(validBooking.getItem())
                .booker(validBooking.getBooker())
                .start(validBooking.getStart())
                .end(validBooking.getEnd())
                .status(Status.APPROVED)
                .build();

        BookingDto bookingDtoExpected = BookingDto.builder()
                .id(1L)
                .start(testTime.plusDays(1))
                .end(testTime.plusDays(2))
                .status(Status.APPROVED)
                .booker(userMapper.toDto(booker))
                .item(itemMapper.toDto(item))
                .build();

        when(bookingMapper.bookingToDto(bookingWithUpdateStatus)).thenReturn(bookingDtoExpected);

        assertEquals(Status.APPROVED,
                bookingService.update(1L, 1L, true).getStatus());
    }

    @Test
    public void updateBookingNotByOwnerTest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(validBooking));

        assertThrows(BookingByOwnerException.class,
                () -> bookingService.update(1L, 2L, true));
    }

    @Test
    public void getByIdTest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(validBooking));

        BookingDto bookingDtoExpected = BookingDto.builder()
                .id(1L)
                .start(testTime.plusDays(1))
                .end(testTime.plusDays(2))
                .status(Status.WAITING)
                .booker(userMapper.toDto(booker))
                .item(itemMapper.toDto(item))
                .build();

        when(bookingMapper.bookingToDto(validBooking)).thenReturn(bookingDtoExpected);

        assertEquals(bookingDtoExpected, bookingService.getById(1L, 2L));
    }

    @Test
    public void getByIdFailByRandomUserTest() {
        User randomUser = User.builder()
                .id(10L)
                .name("random")
                .email("random@email.com")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(randomUser));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(validBooking));

        assertThrows(BookingByOwnerException.class, () -> bookingService.getById(1L, 10L));
    }
}
