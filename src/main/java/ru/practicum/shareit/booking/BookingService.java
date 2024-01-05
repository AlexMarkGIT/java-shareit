package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReqDto;

import java.util.List;

public interface BookingService {

    BookingDto create(BookingReqDto bookingReqDto, Long userId);

    BookingDto update(Long bookingId, Long userId, Boolean approved);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getAllByBooker(Long userId, State state);

    List<BookingDto> getAllByOwner(Long userId, State state);
}
