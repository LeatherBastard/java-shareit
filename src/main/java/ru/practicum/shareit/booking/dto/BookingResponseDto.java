package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingResponseDto {
    private Integer id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Integer itemId;

    private String status;

    private ItemBookingDto item;
    private UserBookingDto booker;
}
