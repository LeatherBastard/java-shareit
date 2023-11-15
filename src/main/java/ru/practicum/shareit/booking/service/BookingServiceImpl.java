package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingView;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
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
    public BookingView add(int bookerId, BookingDto bookingDto) {
        Optional<User> optionalBooker = userRepository.findById(bookerId);
        if (!optionalBooker.isPresent())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, bookerId);
        Optional<Item> optionalItem = itemRepository.findById(bookingDto.getItemId());
        if (!optionalItem.isPresent())
            throw new EntityNotFoundException(ITEM_NOT_FOUND_MESSAGE, bookingDto.getItemId());
        Item item = optionalItem.get();
        if (!item.getAvailable()) {
            throw new ItemUnavailableException(ITEM_NOT_AVAILABLE_MESSAGE, item.getId());
        }


        if (optionalItem.get().getOwner().getId() == bookerId)
            throw new BookingOwnerEqualsBookerException(BOOKING_OWNER_EQUALS_BOOKER_MESSAGE);

        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new BookingDateValidationException(START_DATE_EQUALS_END_DATE_MESSAGE);
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingDateValidationException(START_DATE_BEFORE_CURRENT_DATE_MESSAGE);
        }
        if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BookingDateValidationException(END_DATE_BEFORE_CURRENT_DATE_MESSAGE);
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BookingDateValidationException(END_DATE_BEFORE_START_DATE_MESSAGE);
        }

        Booking booking = mapper.mapToBooking(bookingDto);
        booking.setItem(optionalItem.get());
        booking.setBooker(optionalBooker.get());
        booking.setStatus(BookingStatus.WAITING);
        return mapper.mapToBookingView(bookingRepository.save(booking));
    }

    @Override
    public BookingView getById(Integer userId, Integer bookingId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, userId);


        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (!optionalBooking.isPresent())
            throw new EntityNotFoundException(BOOKING_NOT_FOUND_MESSAGE, bookingId);

        boolean isUserIdEqualsBookerOrItemOwnerId = optionalUser.get().getId() == optionalBooking.get().getBooker().getId() ||
                optionalUser.get().getId() == optionalBooking.get().getItem().getOwner().getId();

        if (!isUserIdEqualsBookerOrItemOwnerId)
            throw new WrongOwnerOrBookerException(WRONG_OWNER_OR_BOOKER_MESSAGE);

        return mapper.mapToBookingView(optionalBooking.get());
    }


    @Override
    public List<BookingView> getAllByBooker(int bookerId, String state) {
        Optional<User> optionalBooker = userRepository.findById(bookerId);
        if (!optionalBooker.isPresent())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, bookerId);
        List<BookingView> result;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                result = bookingRepository
                        .findAllByBookerOrderByStartDesc(optionalBooker.get())
                        .stream().map(mapper::mapToBookingView)
                        .collect(Collectors.toList());
                break;
            case "CURRENT":
                result = bookingRepository
                        .findAllByBooker_IdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(bookerId, now, now)
                        .stream().map(mapper::mapToBookingView)
                        .collect(Collectors.toList());
                break;
            case "PAST":
                result = bookingRepository
                        .findAllByBooker_IdAndEndLessThanOrderByStartDesc(bookerId, now)
                        .stream().map(mapper::mapToBookingView)
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                result = bookingRepository
                        .findAllFutureBookingsByUser(bookerId)
                        .stream().map(mapper::mapToBookingView)
                        .collect(Collectors.toList());
                break;
            case "WAITING":
            case "REJECTED":
                result = bookingRepository
                        .findAllByBookerAndStatusOrderByStartDesc(optionalBooker.get(), BookingStatus.valueOf(state))
                        .stream().map(mapper::mapToBookingView)
                        .collect(Collectors.toList());
                break;
            default:
                throw new UnsupportedStatusException(STATUS_NOT_SUPPORTED_MESSAGE, state);
        }

        return result;
    }

    @Override
    public List<BookingView> getAllByItemsOwner(int userId, String state) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, userId);

        List<Item> userItems = itemRepository.findAllByOwner(optionalUser.get());

        List<BookingView> result;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                result = userItems.stream()
                        .map(bookingRepository::findAllByItemOrderByStartDesc)
                        .flatMap(Collection::stream)
                        .map(mapper::mapToBookingView)
                        .collect(Collectors.toList());
                break;
            case "CURRENT":
                result = userItems.stream()
                        .map(item -> bookingRepository.findAllByItem_IdAndStartLessThanEqualAndEndGreaterThanOrderByStartDesc(item.getId(), now, now))
                        .flatMap(Collection::stream)
                        .map(mapper::mapToBookingView)
                        .collect(Collectors.toList());
                break;
            case "PAST":
                result = userItems.stream()
                        .map(item -> bookingRepository.findAllByItem_IdAndEndLessThanOrderByStartDesc(item.getId(), now))
                        .flatMap(Collection::stream)
                        .map(mapper::mapToBookingView)
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                result = userItems.stream()
                        .map(item -> bookingRepository.findAllFutureBookingsByItem(item.getId()))
                        .flatMap(Collection::stream)
                        .map(mapper::mapToBookingView)
                        .collect(Collectors.toList());
                break;
            case "WAITING":
            case "REJECTED":
                result = userItems.stream()
                        .map(item -> bookingRepository.findAllByItemAndStatusOrderByStartDesc(item, BookingStatus.valueOf(state)))
                        .flatMap(Collection::stream)
                        .map(mapper::mapToBookingView)
                        .collect(Collectors.toList());
                break;
            default:
                throw new UnsupportedStatusException(STATUS_NOT_SUPPORTED_MESSAGE, state);
        }

        return result;
    }

    @Override
    public BookingView updateBookingStatus(int userId, int bookingId, boolean approved) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (!optionalBooking.isPresent())
            throw new EntityNotFoundException(BOOKING_NOT_FOUND_MESSAGE, bookingId);
        Booking oldBooking = optionalBooking.get();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, userId);
        User owner = optionalUser.get();

        if (oldBooking.getItem().getOwner().getId() != owner.getId()) {
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
