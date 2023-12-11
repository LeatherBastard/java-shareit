package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BookingDateValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    public static final String START_DATE_EQUALS_END_DATE_MESSAGE = "Start date equals end date!";

    public static final String START_DATE_BEFORE_CURRENT_DATE_MESSAGE = "Start date before current date!";
    public static final String END_DATE_BEFORE_CURRENT_DATE_MESSAGE = "End date before current date!";

    public static final String END_DATE_BEFORE_START_DATE_MESSAGE = "End date before start date!";


    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto bookingRequestDto) {

        if (bookingRequestDto.getStart().equals(bookingRequestDto.getEnd())) {
            throw new BookingDateValidationException(START_DATE_EQUALS_END_DATE_MESSAGE);
        }
        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingDateValidationException(START_DATE_BEFORE_CURRENT_DATE_MESSAGE);
        }
        if (bookingRequestDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BookingDateValidationException(END_DATE_BEFORE_CURRENT_DATE_MESSAGE);
        }
        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart())) {
            throw new BookingDateValidationException(END_DATE_BEFORE_START_DATE_MESSAGE);
        }

        log.info("Creating booking {}, userId={}", bookingRequestDto, userId);
        return bookingClient.bookItem(userId, bookingRequestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable("bookingId") int bookingId, @RequestParam boolean approved) {
        log.info("Update booking {}, userId={}", bookingId, userId);
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByItemsOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get bookings from user items with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsByItemsOwner(userId, state, from, size);
    }


}
