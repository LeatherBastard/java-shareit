package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemView;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class ItemBookingDatesView {
    private Integer id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private Integer request;
    private BookingItemView lastBooking;
    private BookingItemView nextBooking;

    private Set<CommentView> comments;
}
