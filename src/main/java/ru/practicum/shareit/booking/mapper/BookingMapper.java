package ru.practicum.shareit.booking.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemView;
import ru.practicum.shareit.booking.dto.BookingView;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookingView;
import ru.practicum.shareit.user.dto.UserBookingView;

@Component
public class BookingMapper {
    public BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .build();
    }

    public BookingView mapToBookingView(Booking booking) {
        return BookingView.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .status(booking.getStatus().name())
                .item(
                        ItemBookingView.builder()
                                .id(booking.getItem().getId())
                                .name(booking.getItem().getName()).build()
                )
                .booker(UserBookingView.builder().id(booking.getBooker().getId()).build())
                .build();
    }


    public BookingItemView mapToBookingItemView(Booking booking) {
        return BookingItemView.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }


    public Booking mapToBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }
}
