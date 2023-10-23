package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    private int itemId;
    private LocalDate date;
    private boolean confirmed;

}
