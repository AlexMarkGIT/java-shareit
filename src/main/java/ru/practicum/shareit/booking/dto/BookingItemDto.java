package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingItemDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private Long bookerId;
    private Long itemId;
}
