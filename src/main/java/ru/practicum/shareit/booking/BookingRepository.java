package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository {
    List<Booking> getAll();

    Booking getById(Integer id);

    Booking add(Booking booking);

    Booking update(Booking booking);

    void removeAll();
}
