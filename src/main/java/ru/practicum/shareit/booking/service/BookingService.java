package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingView;

import java.util.List;

public interface BookingService {
    BookingView add(Integer bookerId, BookingDto booking);

    BookingView getById(Integer userId, Integer id);

    List<BookingView> getAllByBooker(Integer bookerId, String state);

    List<BookingView> getAllByItemsOwner(Integer userId, String state);

    BookingView updateBookingStatus(Integer userId, Integer bookingId, boolean approved);
}
