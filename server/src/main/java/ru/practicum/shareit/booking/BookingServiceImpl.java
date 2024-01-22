package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.exception.BookingByOwnerException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto create(BookingReqDto bookingReqDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));
        Item item = itemRepository.findById(bookingReqDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена с id: " + bookingReqDto.getItemId()));

        if (item.getOwner().equals(user)) {
            throw new BookingByOwnerException("Владелец не может бронировать свои вещи");
        }

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Вещь недоступна для бронирования");
        }

        if (!checkAvailableTimeForBooking(item.getId(), bookingReqDto.getStart(), bookingReqDto.getEnd())) {
            throw new ItemNotAvailableException("Время для бронирования уже занято");
        }

        Booking newBooking = bookingMapper.bookingReqDtoToBooking(bookingReqDto);
        newBooking.setBooker(user);
        newBooking.setItem(item);
        newBooking.setStatus(Status.WAITING);

        return bookingMapper.bookingToDto(bookingRepository.save(newBooking));
    }

    @Override
    public BookingDto update(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено с таким id" + bookingId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BookingException("Статус бронирования уже подтвержден");
        }

        if (!booking.getItem().getOwner().getId().equals(user.getId())) {
            throw new BookingByOwnerException("Пользователь не является владельцем вещи");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
            bookingRepository.save(booking);
        } else {
            booking.setStatus(Status.REJECTED);
            bookingRepository.save(booking);
        }

        return bookingMapper.bookingToDto(booking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено с таким id: " + bookingId));

        if (!booking.getBooker().getId().equals(user.getId()) &&
        !booking.getItem().getOwner().getId().equals(user.getId())) {
            throw new BookingByOwnerException("Пользователь не является арендатором или владельцем вещи");
        }

        return bookingMapper.bookingToDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBooker(Long userId, State state, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));

        LocalDateTime currentTime = LocalDateTime.now().withNano(0);

        Pageable pageable = PageRequest.of(from / size, size);

        switch (state) {
            case ALL:
                return mapToBookingDtoList(bookingRepository.findAllByBooker(user.getId(), pageable));
            case CURRENT:
                return mapToBookingDtoList(bookingRepository
                        .findAllCurrentByBooker(userId, currentTime, pageable));
            case PAST:
                return mapToBookingDtoList(bookingRepository
                        .findAllPastByBooker(userId, currentTime, pageable));
            case FUTURE:
                return mapToBookingDtoList(bookingRepository
                        .findAllFutureByBooker(userId, currentTime, pageable));
            case WAITING:
                return mapToBookingDtoList(bookingRepository
                        .findAllWaitingByBooker(userId, currentTime, pageable));
            case REJECTED:
                return mapToBookingDtoList(bookingRepository
                        .findAllRejectedByBooker(userId, pageable));
            default:
                throw new BookingException("Нераспознаный параметр запроса");
        }
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, State state, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id: " + userId));

        LocalDateTime currentTime = LocalDateTime.now().withNano(0);

        Pageable pageable = PageRequest.of(from / size, size);

        switch (state) {
            case ALL:
                return mapToBookingDtoList(bookingRepository
                        .findAllByOwner(user.getId(), pageable));
            case CURRENT:
                return mapToBookingDtoList(bookingRepository
                        .findAllCurrentByOwner(userId, currentTime, pageable));
            case PAST:
                return mapToBookingDtoList(bookingRepository
                        .findAllPastByOwner(userId, currentTime, pageable));
            case FUTURE:
                return mapToBookingDtoList(bookingRepository
                        .findAllFutureByOwner(userId, currentTime, pageable));
            case WAITING:
                return mapToBookingDtoList(bookingRepository
                        .findAllWaitingByOwner(userId, currentTime, pageable));
            case REJECTED:
                return mapToBookingDtoList(bookingRepository
                        .findAllRejectedByOwner(userId, pageable));
            default:
                throw new BookingException("Нераспознаный параметр запроса");

        }
    }

    private Boolean checkAvailableTimeForBooking(Long itemId, LocalDateTime start, LocalDateTime end) {
        List<Booking> bookingsOfItem = bookingRepository.findAllByItem_Id(itemId);

        System.out.println("\n" + bookingsOfItem + "\n");

        if (bookingsOfItem.size() == 0) {
            return true;
        }

        for (Booking booking : bookingsOfItem) {
            if (booking.getStatus().equals(Status.APPROVED) &&
                    !(start.isAfter(booking.getEnd()) || end.isBefore(booking.getStart()))) {
                return false;
            }
        }

        return true;
    }

    private List<BookingDto> mapToBookingDtoList(List<Booking> bookings) {
        List<BookingDto> bookingDtoList = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtoList.add(bookingMapper.bookingToDto(booking));
        }

        return bookingDtoList;
    }
}
