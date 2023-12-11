package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto add(int bookerId, BookingRequestDto booking);

    BookingResponseDto getById(int userId, int id);

    List<BookingResponseDto> getAllByBooker(int bookerId, String state, int from, int size);

    List<BookingResponseDto> getAllByItemsOwner(int userId, String state, int from, int size);

    BookingResponseDto updateBookingStatus(int userId, int bookingId, boolean approved);
}
