package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class ItemResponseDto {
    private Integer id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private Integer request;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;

    private Set<CommentResponseDto> comments;
}
