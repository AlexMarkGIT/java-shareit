package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BookingException;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@RestController
@Validated
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid BookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Бронирование вещи id: " + bookingDto.getItemId() + ", пользователем id: " + userId);
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new BookingException("Время старта должно быть раньше времени окончания");
        }

        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@PathVariable Long bookingId,
                             @RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestParam Boolean approved) {
        log.info("Обновление статуса бронирования id " + bookingId + ", пользователем id: " + userId);

        return bookingClient.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "state",
                                                   required = false,
                                                   defaultValue = "ALL") State state,
                                           @RequestParam(name = "from",
                                                   defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(name = "size",
                                                   defaultValue = "10") @Min(1) Integer size) {
        return bookingClient.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "state",
                                                   required = false,
                                                   defaultValue = "ALL") State state,
                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingClient.getAllByOwner(userId, state, from, size);
    }

}
