package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String LOGGER_GET_BOOKINGS_BY_USER_MESSAGE = "Returning bookings by user";
    private static final String LOGGER_GET_BOOKINGS_BY_USER_ITEMS_MESSAGE = "Returning bookings by user items";
    private static final String LOGGER_ADD_BOOKING_MESSAGE = "Adding booking";

    private static final String LOGGER_GET_BOOKING_BY_ID_MESSAGE = "Getting booking with id: {}";
    private static final String LOGGER_UPDATE_BOOKING_MESSAGE = "Updating booking with id: {}";
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto addBookingRequest(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info(LOGGER_ADD_BOOKING_MESSAGE);
        return bookingService.add(userId, bookingRequestDto);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @PathVariable("bookingId") int bookingId) {
        log.info(LOGGER_GET_BOOKING_BY_ID_MESSAGE, bookingId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @RequestParam(defaultValue = "ALL") String state, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "20") int size) {
        log.info(LOGGER_GET_BOOKINGS_BY_USER_MESSAGE);
        return bookingService.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByItemsOwner(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @RequestParam(defaultValue = "ALL") String state, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "20") int size) {
        log.info(LOGGER_GET_BOOKINGS_BY_USER_ITEMS_MESSAGE);
        return bookingService.getAllByItemsOwner(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBookingStatus(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @PathVariable("bookingId") int bookingId, @RequestParam boolean approved) {
        log.info(LOGGER_UPDATE_BOOKING_MESSAGE, bookingId);
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }
}
