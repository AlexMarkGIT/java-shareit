package ru.practicum.shareit.booking;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    @NotNull
    private Long itemId;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
}
