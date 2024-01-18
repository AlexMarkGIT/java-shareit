package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private ItemMapper itemMapper;

    private final User owner = new User(1L, "owner","owner@email.com");
    private final User requester = new User(2L, "requester","requester@email.com");
    private final User booker = new User(3L, "booker","booker@email.com");

    private final ItemRequest request =
            new ItemRequest(1L, "desc", LocalDateTime.now(), requester);
    private final Item item = new Item(null, "name", "desc",
            true, owner, request);
    private final Item itemFromRepo = new Item(1L, "name", "desc",
            true, owner, request);

    private final ItemDto itemDtoReq = ItemDto.builder()
            .name("name")
            .description("desc")
            .available(true)
            .requestId(1L)
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("name")
            .description("desc")
            .available(true)
            .requestId(1L)
            .build();

    private final ItemBookingDto itemBookingDto = ItemBookingDto.builder()
            .id(1L)
            .name("name")
            .description("desc")
            .available(true)
            .build();

    @Test
    public void createItemTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemMapper.toEntity(any(ItemDto.class))).thenReturn(item);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(itemFromRepo);
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);

        assertEquals(itemDto, itemService.create(itemDtoReq, 1L));
    }

    @Test
    public void updateItemTest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemFromRepo));
        when(itemRepository.save(any(Item.class))).thenReturn(itemFromRepo);
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);

        assertEquals(itemDto, itemService.update(itemDto, 1L));
    }

    @Test
    public void getByIdTest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemMapper.toItemBookingDto(item)).thenReturn(itemBookingDto);
        when(bookingRepository.findAllByItem_Id(1L)).thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItem_Id(1L)).thenReturn(Collections.emptyList());

        assertEquals(itemBookingDto, itemService.getById(1L, 1L));
    }

    @Test
    public void getByUserIdTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));
        when(itemMapper.toItemBookingDto(item)).thenReturn(itemBookingDto);
        when(bookingRepository.findAllByItem_Id(1L)).thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItem_Id(1L)).thenReturn(Collections.emptyList());

        assertEquals(List.of(itemBookingDto), itemService.getByUserId(1L));
    }

    @Test
    public void searchTest() {
        when(itemRepository.search(anyString())).thenReturn(List.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        assertTrue(itemService.search("name").contains(itemDto));
    }

    @Test
    public void createComment() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        LocalDateTime testTime = LocalDateTime.now();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .created(testTime)
                .authorId(3L)
                .authorName("booker")
                .itemId(1L)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .created(testTime)
                .author(booker)
                .item(item)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(itemFromRepo));
        when(userRepository.findById(3L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemAndBooker(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(itemMapper.toComment(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(itemMapper.toCommentDto(comment)).thenReturn(commentDto);

        assertEquals(commentDto, itemService.createComment(commentDto));
    }
}
