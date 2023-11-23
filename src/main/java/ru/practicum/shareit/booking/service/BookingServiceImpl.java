package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.service.ItemServiceImpl.ITEM_NOT_FOUND_MESSAGE;
import static ru.practicum.shareit.user.service.UserServiceImpl.USER_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {


    public static final String BOOKING_NOT_FOUND_MESSAGE = "Booking with id %d not found";

    public static final String BOOKING_STATUS_ALREADY_ACCEPTED_OR_REJECTED_MESSAGE = "Booking status was already accepted or rejected";
    private static final String BOOKING_OWNER_EQUALS_BOOKER_MESSAGE = "You cannot be an owner and a booker of the item at the same time";
    public static final String ITEM_NOT_AVAILABLE_MESSAGE = "Item with id %d not available";

    public static final String STATUS_NOT_SUPPORTED_MESSAGE = "Unknown state: %s";

    public static final String START_DATE_EQUALS_END_DATE_MESSAGE = "Start date equals end date!";

    public static final String START_DATE_BEFORE_CURRENT_DATE_MESSAGE = "Start date before current date!";
    public static final String END_DATE_BEFORE_CURRENT_DATE_MESSAGE = "End date before current date!";

    public static final String END_DATE_BEFORE_START_DATE_MESSAGE = "End date before start date!";
    private static final String WRONG_OWNER_MESSAGE = "You are not an owner ot this item!";
    private static final String WRONG_OWNER_OR_BOOKER_MESSAGE = "You are not an owner or booker!";

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper mapper;

    @Override
    public BookingResponseDto add(int bookerId, BookingRequestDto bookingRequestDto) {
        Optional<User> optionalBooker = userRepository.findById(bookerId);
        if (optionalBooker.isEmpty())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, bookerId);
        Optional<Item> optionalItem = itemRepository.findById(bookingRequestDto.getItemId());
        if (optionalItem.isEmpty())
            throw new EntityNotFoundException(ITEM_NOT_FOUND_MESSAGE, bookingRequestDto.getItemId());
        Item item = optionalItem.get();
        if (!item.getAvailable()) {
            throw new ItemUnavailableException(ITEM_NOT_AVAILABLE_MESSAGE, item.getId());
        }

        if (optionalItem.get().getOwner().getId().equals(bookerId))
            throw new BookingOwnerEqualsBookerException(BOOKING_OWNER_EQUALS_BOOKER_MESSAGE);

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

        Booking booking = mapper.mapToBooking(bookingRequestDto);
        booking.setItem(optionalItem.get());
        booking.setBooker(optionalBooker.get());
        booking.setStatus(BookingStatus.WAITING);
        return mapper.mapToBookingView(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getById(int userId, int bookingId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, userId);


        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty())
            throw new EntityNotFoundException(BOOKING_NOT_FOUND_MESSAGE, bookingId);

        boolean isUserIdEqualsBookerOrItemOwnerId = optionalUser.get().getId().equals(optionalBooking.get().getBooker().getId()) ||
                optionalUser.get().getId().equals(optionalBooking.get().getItem().getOwner().getId());

        if (!isUserIdEqualsBookerOrItemOwnerId)
            throw new WrongOwnerOrBookerException(WRONG_OWNER_OR_BOOKER_MESSAGE);

        return mapper.mapToBookingView(optionalBooking.get());
    }


    @Override
    public List<BookingResponseDto> getAllByBooker(int bookerId, String state, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new PaginationBoundariesException(from, size);
        }

        Optional<User> optionalBooker = userRepository.findById(bookerId);
        if (optionalBooker.isEmpty())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, bookerId);

        LocalDateTime now = LocalDateTime.now();
        BooleanExpression byBookerId = QBooking.booking.booker.eq(optionalBooker.get());
        Sort sort = Sort.by("start").descending();
        PageRequest pageRequest = PageRequest.of(from, size, sort);
        Iterable<Booking> bookings;

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAll(byBookerId, pageRequest);
                break;
            case "CURRENT":
                BooleanExpression byStartLessThan = QBooking.booking.start.lt(now);
                BooleanExpression byEndGreaterThan = QBooking.booking.end.gt(now);
                bookings = bookingRepository.findAll(byBookerId.and(byStartLessThan).and(byEndGreaterThan), pageRequest);
                break;
            case "PAST":
                BooleanExpression byEndLessThan = QBooking.booking.end.lt(now);
                bookings = bookingRepository.findAll(byBookerId.and(byEndLessThan), pageRequest);
                break;
            case "FUTURE":

                BooleanExpression byStartGreaterThan = QBooking.booking.start.gt(now);
                bookings = bookingRepository.findAll(byBookerId.and(byStartGreaterThan), pageRequest);
                break;
            case "WAITING":
            case "REJECTED":
                BooleanExpression byStatus = QBooking.booking.status.eq(BookingStatus.valueOf(state));
                bookings = bookingRepository.findAll(byBookerId.and(byStatus), pageRequest);
                break;
            default:
                throw new UnsupportedStatusException(STATUS_NOT_SUPPORTED_MESSAGE, state);
        }
        return mapper.mapToBookingsFromIterable(bookings)
                .stream()
                .map(mapper::mapToBookingView)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllByItemsOwner(int userId, String state, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new PaginationBoundariesException(from, size);
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, userId);

        List<Item> userItems = itemRepository.findAllByOwner(optionalUser.get());

        LocalDateTime now = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();
        PageRequest pageRequest = PageRequest.of(from, size, sort);
        Iterable<Booking> bookings;
        List<Booking> result = new ArrayList<>();

        switch (state) {
            case "ALL":

                for (Item item : userItems) {
                    List<Booking> itemBookings = mapper
                            .mapToBookingsFromIterable(bookingRepository.findAll(QBooking.booking.item.eq(item), pageRequest));
                    result.addAll(itemBookings);
                }
                break;
            case "CURRENT":

                BooleanExpression byStartLessThanEqual = QBooking.booking.start.loe(now);
                BooleanExpression byEndGreaterThan = QBooking.booking.end.gt(now);
                for (Item item : userItems) {
                    List<Booking> itemBookings = mapper
                            .mapToBookingsFromIterable(bookingRepository
                                    .findAll(QBooking.booking.item.eq(item).and(byStartLessThanEqual).and(byEndGreaterThan), pageRequest));
                    result.addAll(itemBookings);
                }
                break;
            case "PAST":

                BooleanExpression byEndLessThan = QBooking.booking.end.lt(now);
                for (Item item : userItems) {
                    List<Booking> itemBookings = mapper
                            .mapToBookingsFromIterable(bookingRepository
                                    .findAll(QBooking.booking.item.eq(item).and(byEndLessThan), pageRequest));
                    result.addAll(itemBookings);
                }
                break;
            case "FUTURE":

                BooleanExpression byStartGreaterThanEqual = QBooking.booking.start.goe(now);
                for (Item item : userItems) {
                    List<Booking> itemBookings = mapper
                            .mapToBookingsFromIterable(bookingRepository
                                    .findAll(QBooking.booking.item.eq(item).and(byStartGreaterThanEqual), pageRequest));
                    result.addAll(itemBookings);
                }
                break;
            case "WAITING":
            case "REJECTED":

                BooleanExpression byStatus = QBooking.booking.status.eq(BookingStatus.valueOf(state));
                for (Item item : userItems) {
                    List<Booking> itemBookings = mapper
                            .mapToBookingsFromIterable(bookingRepository
                                    .findAll(QBooking.booking.item.eq(item).and(byStatus), pageRequest));
                    result.addAll(itemBookings);
                }
                break;
            default:
                throw new UnsupportedStatusException(STATUS_NOT_SUPPORTED_MESSAGE, state);
        }
        return result.stream().map(mapper::mapToBookingView)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDto updateBookingStatus(int userId, int bookingId, boolean approved) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty())
            throw new EntityNotFoundException(BOOKING_NOT_FOUND_MESSAGE, bookingId);
        Booking oldBooking = optionalBooking.get();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, userId);
        User owner = optionalUser.get();

        if (!oldBooking.getItem().getOwner().getId().equals(owner.getId())) {
            throw new WrongOwnerOrBookerException(WRONG_OWNER_MESSAGE);
        }

        if (oldBooking.getStatus().equals(BookingStatus.APPROVED) || oldBooking.getStatus().equals(BookingStatus.REJECTED)) {
            throw new BookingStatusAlreadyAcceptedOrRejectedException(BOOKING_STATUS_ALREADY_ACCEPTED_OR_REJECTED_MESSAGE);
        }

        if (approved) {
            oldBooking.setStatus(BookingStatus.APPROVED);
        } else {
            oldBooking.setStatus(BookingStatus.REJECTED);
        }
        return mapper.mapToBookingView(bookingRepository.save(oldBooking));
    }
}
