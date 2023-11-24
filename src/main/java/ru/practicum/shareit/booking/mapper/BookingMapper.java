package ru.practicum.shareit.booking.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;


@Component
public class BookingMapper {
    public BookingRequestDto mapToBookingDto(Booking booking) {
        return BookingRequestDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .build();
    }

    public BookingResponseDto mapToBookingView(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .status(booking.getStatus().name())
                .item(
                        ItemBookingDto.builder()
                                .id(booking.getItem().getId())
                                .name(booking.getItem().getName()).build()
                )
                .booker(UserBookingDto.builder().id(booking.getBooker().getId()).build())
                .build();
    }


    public BookingItemDto mapToBookingItemView(Booking booking) {
        return BookingItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }


    public Booking mapToBooking(BookingRequestDto bookingRequestDto) {
        return Booking.builder()
                .id(bookingRequestDto.getId())
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .build();
    }

}
