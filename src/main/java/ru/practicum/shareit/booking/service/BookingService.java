package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingView;

import java.util.List;

public interface BookingService {
    BookingView add(int bookerId, BookingDto booking);

    BookingView getById(Integer userId, Integer id);

    List<BookingView> getAllByBooker(int bookerId, String state);

    List<BookingView> getAllByItemsOwner(int userId, String state);

    BookingView updateBookingStatus(int userId, int bookingId, boolean approved);
}
