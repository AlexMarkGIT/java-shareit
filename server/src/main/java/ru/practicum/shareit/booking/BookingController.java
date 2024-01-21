package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReqDto;
import ru.practicum.shareit.exception.BookingException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
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
                                                   defaultValue = "ALL") State state,
                                           @RequestParam(name = "from",
                                                   defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(name = "size",
                                                   defaultValue = "10") @Min(1) Integer size) {
        return bookingService.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public  List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "state",
                                                   required = false,
                                                   defaultValue = "ALL") State state,
                                           @RequestParam(name = "from", defaultValue = "0") @Min(0)Integer from,
                                           @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        return bookingService.getAllByOwner(userId, state, from, size);
    }

}
