package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    public final UserMapper userMapper;
    public final ItemMapper itemMapper;

    public BookingDto bookingToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(userMapper.toDto(booking.getBooker()))
                .item(itemMapper.toDto(booking.getItem()))
                .build();
    }

    public Booking bookingReqDtoToBooking(BookingReqDto bookingReqDto) {
        return Booking.builder()
                .start(bookingReqDto.getStart())
                .end(bookingReqDto.getEnd())
                .build();
    }

    public BookingItemDto toBookingItemDto(Booking booking) {
        if (booking != null) {
        return BookingItemDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .bookerId(booking.getBooker().getId())
                .itemId(booking.getItem().getId())
                .build();
        } else {
            return null;
        }
    }

}
