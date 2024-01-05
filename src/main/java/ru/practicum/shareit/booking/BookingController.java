package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.exception.BookingException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody @Valid BookingReqDto bookingReqDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Бронирование вещи id: " + bookingReqDto.getItemId() + ", пользователем id: " + userId
                + ".\nВремя старта/конца: " + bookingReqDto.getStart() + "/" + bookingReqDto.getEnd());
        if (bookingReqDto.getStart().isAfter(bookingReqDto.getEnd()) ||
        bookingReqDto.getStart().equals(bookingReqDto.getEnd())) {
            throw new BookingException("Время старта должно быть раньше времени окончания");
        }
        BookingDto bookingDto = bookingService.create(bookingReqDto, userId);
        log.info("Бронирование завершено с id: " + bookingDto.getId());
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId,
                             @RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestParam Boolean approved) {
        log.info("Обновление статуса бронирования id " + bookingId + ", пользователем id: " + userId);
        BookingDto bookingDto = bookingService.update(bookingId, userId, approved);
        log.info("Обновление завершено");
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "state",
                                                   required = false,
                                                   defaultValue = "ALL") String state) {
        try {
            State reqState = State.valueOf(state);
            return bookingService.getAllByBooker(userId, reqState);
        } catch (IllegalArgumentException e) {
            throw new BookingException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @GetMapping("/owner")
    public  List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "state",
                                                   required = false,
                                                   defaultValue = "ALL") String state) {
        try {
            State reqState = State.valueOf(state);
            return bookingService.getAllByOwner(userId, reqState);
        } catch (IllegalArgumentException e) {
            throw new BookingException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

}
