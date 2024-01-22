package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User itemOwner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));

        Item item = itemMapper.toEntity(itemDto);

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден с id: " + itemDto.getRequestId()));
            item.setRequest(itemRequest);
        }

        item.setOwner(itemOwner);

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId) {

        Item item = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена с id: " + itemDto.getId()));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вещь не принадлежит пользователю с id: " + userId);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemBookingDto getById(Long itemId, Long userId) {

        LocalDateTime now = LocalDateTime.now().withNano(0);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена с таким id " + itemId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));

        if (item.getOwner().getId().equals(user.getId())) {
            return setComments(setLastNextBookingsToItem(itemMapper.toItemBookingDto(item), now));
        } else {
            return setComments(itemMapper.toItemBookingDto(item));
        }
    }

    @Override
    public List<ItemBookingDto> getByUserId(Long userId) {

        LocalDateTime now = LocalDateTime.now().withNano(0);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));

        List<Item> items = itemRepository.findAllByOwnerId(user.getId());
        List<ItemBookingDto> itemBookingDtoList = new ArrayList<>();

        if (items.size() != 0) {
            for (Item item : items) {
                itemBookingDtoList.add(setComments(setLastNextBookingsToItem(itemMapper
                        .toItemBookingDto(item), now)));
            }
        }

        return itemBookingDtoList;
    }

    @Override
    public List<ItemDto> search(String searchText) {
        List<ItemDto> items = new ArrayList<>();
        if (searchText.isBlank()) {
            return items;
        }
        for (Item item : itemRepository.search(searchText)) {
            items.add(itemMapper.toDto(item));
        }
        return items;
    }

    @Override
    public CommentDto createComment(CommentDto commentDto) {

        User user = userRepository.findById(commentDto.getAuthorId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + commentDto.getAuthorId()));
        Item item = itemRepository.findById(commentDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена с таким id " + commentDto.getItemId()));
        List<Booking> bookings = bookingRepository
                .findByItemAndBooker(user.getId(), item.getId(), LocalDateTime.now());

        if (bookings.size() == 0) {
            throw new BookingException("У данного пользователя нет бронирований этой вещи");
        }

        Comment comment = itemMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);

        return itemMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<ItemDto> getByRequest(Long requestId) {
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        List<ItemDto> itemDtoList = new ArrayList<>();
        if (items.size() != 0) {
            for (Item item : items) {
                itemDtoList.add(itemMapper.toDto(item));
            }
        }
        return itemDtoList;
    }

    private ItemBookingDto setLastNextBookingsToItem(ItemBookingDto itemBookingDto,
                                                     LocalDateTime now) {

        List<Booking> bookingsForItem = bookingRepository.findAllByItem_Id(itemBookingDto.getId());
        if (bookingsForItem.size() != 0) {
            Booking lastBooking = bookingsForItem.stream()
                    .filter(b -> b.getStatus().equals(Status.APPROVED) &&
                            b.getStart().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            Booking nextBooking = bookingsForItem.stream()
                    .filter(b -> b.getStatus().equals(Status.APPROVED) &&
                            b.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            itemBookingDto.setLastBooking(bookingMapper.toBookingItemDto(lastBooking));
            itemBookingDto.setNextBooking(bookingMapper.toBookingItemDto(nextBooking));
        }
        return itemBookingDto;
    }

    private ItemBookingDto setComments(ItemBookingDto itemBookingDto) {
        List<Comment> commentsForItem = commentRepository.findAllByItem_Id(itemBookingDto.getId());
        List<CommentDto> commentDtoList = new ArrayList<>();
        if (commentsForItem.size() != 0) {
            for (Comment comment : commentsForItem) {
                commentDtoList.add(itemMapper.toCommentDto(comment));
            }
        }
        itemBookingDto.setComments(commentDtoList);

        return itemBookingDto;
    }
}
