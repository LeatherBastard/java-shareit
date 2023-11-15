package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemBookingView;
import ru.practicum.shareit.user.dto.UserBookingView;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingView {
    private Integer id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Integer itemId;

    private String status;

    private ItemBookingView item;
    private UserBookingView booker;
}
