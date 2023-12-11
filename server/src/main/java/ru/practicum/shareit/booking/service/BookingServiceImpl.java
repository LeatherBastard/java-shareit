package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingSelectionState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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

        Booking booking = mapper.mapToBooking(bookingRequestDto);
        booking.setItem(optionalItem.get());
        booking.setBooker(optionalBooker.get());
        booking.setStatus(BookingStatus.WAITING);
        return mapper.mapToBookingDto(bookingRepository.save(booking));
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

        return mapper.mapToBookingDto(optionalBooking.get());
    }


    @Override
    public List<BookingResponseDto> getAllByBooker(int bookerId, String state, int from, int size) {

        Optional<User> optionalBooker = userRepository.findById(bookerId);
        if (optionalBooker.isEmpty())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, bookerId);

        List<Booking> result = new ArrayList<>();
        BookingSelectionState selectionState;
        try {
            selectionState = BookingSelectionState.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new UnsupportedStatusException(STATUS_NOT_SUPPORTED_MESSAGE, state);
        }
        switch (selectionState) {
            case ALL:
                result = bookingRepository
                        .findAllByUser(bookerId, from, size);
                break;
            case CURRENT:
                result = bookingRepository
                        .findAllCurrentBookingsByUser(bookerId, from, size);
                break;
            case PAST:
                result = bookingRepository
                        .findAllPastBookingsByUser(bookerId, from, size);
                break;
            case FUTURE:
                result = bookingRepository
                        .findAllFutureBookingsByUser(bookerId, from, size);
                break;
            case WAITING:
            case REJECTED:
                result = bookingRepository
                        .findAllByUserAndStatus(bookerId, selectionState.name(), from, size);
                break;
        }

        return result.stream().map(mapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllByItemsOwner(int userId, String state, int from, int size) {

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, userId);


        List<Booking> result = new ArrayList<>();
        BookingSelectionState selectionState;
        try {
            selectionState = BookingSelectionState.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new UnsupportedStatusException(STATUS_NOT_SUPPORTED_MESSAGE, state);
        }

        switch (selectionState) {
            case ALL:
                result = bookingRepository.findAllByOwnerItems(userId, from, size);
                break;
            case CURRENT:
                result = bookingRepository.findAllCurrentBookingsByOwnerItems(userId, from, size);
                break;
            case PAST:
                result = bookingRepository.findAllPastBookingsByOwnerItems(userId, from, size);
                break;
            case FUTURE:
                result = bookingRepository.findAllFutureBookingsByOwnerItems(userId, from, size);
                break;
            case WAITING:
            case REJECTED:
                result = bookingRepository.findAllBookingsByOwnerItemsAndStatus(userId, state, from, size);
                break;
        }
        return result.stream().map(mapper::mapToBookingDto)
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
        return mapper.mapToBookingDto(bookingRepository.save(oldBooking));
    }
}
