package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class ItemResponseDto {
    private Integer id;

    private String name;

    private String description;

    private Boolean available;
    private Integer request;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;

    private Set<CommentResponseDto> comments;
}
