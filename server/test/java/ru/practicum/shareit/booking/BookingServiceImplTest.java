package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User anotherUser;
    private Item item;
    private Booking booking;


    @BeforeEach
    void setUp() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingServiceImpl(userRepository, itemRepository, bookingRepository, bookingMapper);
        user = new User(1, "Mark", "kostrykinmark@gmail.com");
        anotherUser = new User(2, "John", "johndoe@gmail.com");
        item = new Item(1, "Пылесос", "Пылесос", true, user, null);
        booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, user, BookingStatus.WAITING);
    }

    @Nested
    class BookingServiceAddTests {
        @Test
        void add_whenBookerNotFound_thenEntityNotFoundExceptionThrown() {
            BookingRequestDto bookingRequestDto = BookingRequestDto
                    .builder().start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(1)).itemId(1).build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> bookingService.add(1, bookingRequestDto));
            verify(userRepository, Mockito.times(1)).findById(1);
            verify(bookingRepository, Mockito.never()).save(any(Booking.class));
        }

        @Test
        void add_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
            BookingRequestDto bookingRequestDto = BookingRequestDto
                    .builder().start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(1)).itemId(1).build();
            when(userRepository.findById(anyInt()))
                    .thenReturn(Optional.of(new User(1, "Mark", "kostrykinmark@gmail.com")));
            when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> bookingService.add(1, bookingRequestDto));
            verify(userRepository, Mockito.times(1)).findById(1);
            verify(itemRepository, Mockito.times(1)).findById(1);
            verify(bookingRepository, Mockito.never()).save(any(Booking.class));
        }


        @Test
        void add_whenItemIsNotAvailable_thenItemUnavailableExceptionThrown() {
            BookingRequestDto bookingRequestDto = BookingRequestDto
                    .builder().start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(1)).itemId(1).build();
            item.setAvailable(false);
            when(userRepository.findById(anyInt()))
                    .thenReturn(Optional.of(user));
            when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
            assertThrows(ItemUnavailableException.class, () -> bookingService.add(1, bookingRequestDto));
            verify(userRepository, Mockito.times(1)).findById(1);
            verify(itemRepository, Mockito.times(1)).findById(1);
            verify(bookingRepository, Mockito.never()).save(any(Booking.class));

        }

        @Test
        void add_whenOwnerEqualsBooker_thenBookingOwnerEqualsBookerExceptionThrown() {
            BookingRequestDto bookingRequestDto = BookingRequestDto
                    .builder().start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(1)).itemId(1).build();
            when(userRepository.findById(anyInt()))
                    .thenReturn(Optional.of(user));
            when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
            assertThrows(BookingOwnerEqualsBookerException.class, () -> bookingService.add(1, bookingRequestDto));
            verify(userRepository, Mockito.times(1)).findById(1);
            verify(itemRepository, Mockito.times(1)).findById(1);
            verify(bookingRepository, Mockito.never()).save(any(Booking.class));
        }


        @Test
        void add_whenBookingIsValid_thenReturnBooking() {
            BookingRequestDto bookingRequestDto = BookingRequestDto
                    .builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3)).itemId(1).build();
            when(userRepository.findById(anyInt()))
                    .thenReturn(Optional.of(anotherUser));
            when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
            Booking bookingToSave = bookingMapper.mapToBooking(bookingRequestDto);
            bookingToSave.setItem(item);
            bookingToSave.setBooker(anotherUser);
            bookingToSave.setStatus(BookingStatus.WAITING);
            when(bookingRepository.save(any(Booking.class))).thenReturn(bookingToSave);
            bookingService.add(2, bookingRequestDto);
            verify(userRepository, Mockito.times(1)).findById(2);
            verify(itemRepository, Mockito.times(1)).findById(1);
            verify(bookingRepository, Mockito.times(1)).save(any(Booking.class));
        }
    }

    @Nested
    class BookingServiceGetByIdTest {
        @Test
        void getById_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
            when(userRepository.findById(1)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> bookingService.getById(1, 2));
            verify(bookingRepository, Mockito.never()).findById(anyInt());
        }

        @Test
        void getById_whenBookingNotFound_thenEntityNotFoundExceptionThrown() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            assertThrows(EntityNotFoundException.class, () -> bookingService.getById(1, 2));
        }

        @Test
        void getById_whenUserIdEqualsBookerOrItemOwnerId_thenWrongOwnerOrBookerExceptionThrown() {
            Booking booking = Booking.builder().id(1).start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusHours(2))
                    .booker(user).item(item).status(BookingStatus.WAITING).build();
            when(userRepository.findById(2)).thenReturn(Optional.of(anotherUser));
            when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
            assertThrows(WrongOwnerOrBookerException.class, () -> bookingService.getById(2, 1));
        }

        @Test
        void getById_whenUserAndBookingIsValid_thenReturnBooking() {
            Booking booking = Booking.builder().id(1).start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusHours(2))
                    .booker(user).item(item).status(BookingStatus.WAITING).build();
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
            assertEquals(bookingMapper.mapToBookingDto(booking), bookingService.getById(1, 1));
        }
    }

    @Nested
    class BookingServiceGetAllByBookerTests {

        @Test
        void getAllByBooker_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
            assertThrows(EntityNotFoundException.class,
                    () -> bookingService.getAllByBooker(1, "ALL", 1, 1));
        }

        @Test
        void getAllByBooker_whenStateIsAll_thenFindAllByUser() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            bookingService.getAllByBooker(1, "ALL", 1, 1);
            verify(bookingRepository, Mockito.times(1)).findAllByUser(1, 1, 1);
        }

        @Test
        void getAllByBooker_whenStateIsCurrent_thenFindAllCurrentBookingsByUser() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            bookingService.getAllByBooker(1, "CURRENT", 1, 1);
            verify(bookingRepository, Mockito.times(1)).findAllCurrentBookingsByUser(1, 1, 1);
        }

        @Test
        void getAllByBooker_whenStateIsPast_thenFindAllPastBookingsByUser() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            bookingService.getAllByBooker(1, "PAST", 1, 1);
            verify(bookingRepository, Mockito.times(1)).findAllPastBookingsByUser(1, 1, 1);
        }

        @Test
        void getAllByBooker_whenStateIsFuture_thenFindAllFutureBookingsByUser() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            bookingService.getAllByBooker(1, "FUTURE", 1, 1);
            verify(bookingRepository, Mockito.times(1)).findAllFutureBookingsByUser(1, 1, 1);
        }

        @Test
        void getAllByBooker_whenStateIsWaitingOrRejected_thenFindAllByUserAndStatus() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            bookingService.getAllByBooker(1, "WAITING", 1, 1);
            verify(bookingRepository, Mockito.times(1)).findAllByUserAndStatus(1, "WAITING", 1, 1);
        }

        @Test
        void getAllByBooker_whenStateIsInvalid_thenUnsupportedStatusExceptionThrown() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            assertThrows(UnsupportedStatusException.class, () -> bookingService.getAllByBooker(1, "ABC", 1, 1));
        }
    }

    @Nested
    class BookingServiceGetAllByItemsOwnerTests {

        @Test
        void getAllByItemsOwner_whenUserNotFound_thenEntityNotFoundExceptionThrown() {
            assertThrows(EntityNotFoundException.class,
                    () -> bookingService.getAllByItemsOwner(1, "ALL", 1, 1));
            verify(itemRepository, Mockito.never()).findAllByOwner(any(User.class));
        }

        @Test
        void getAllByItemsOwner_whenStateIsAll_thenFindAllByItemId() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            bookingService.getAllByItemsOwner(1, "ALL", 1, 1);
            verify(bookingRepository, Mockito.atLeast(1)).findAllByOwnerItems(1, 1, 1);
        }

        @Test
        void getAllByItemsOwner_whenStateIsCurrent_thenFindAllCurrentBookingsByItemId() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            bookingService.getAllByItemsOwner(1, "CURRENT", 1, 1);
            verify(bookingRepository, Mockito.atLeast(1)).findAllCurrentBookingsByOwnerItems(1, 1, 1);
        }

        @Test
        void getAllByItemsOwner_whenStateIsPast_thenFindAllPastBookingsByItemId() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            bookingService.getAllByItemsOwner(1, "PAST", 1, 1);
            verify(bookingRepository, Mockito.atLeast(1)).findAllPastBookingsByOwnerItems(1, 1, 1);
        }

        @Test
        void getAllByItemsOwner_whenStateIsFuture_thenFindAllFutureBookingsByItemId() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            bookingService.getAllByItemsOwner(1, "FUTURE", 1, 1);
            verify(bookingRepository, Mockito.atLeast(1)).findAllFutureBookingsByOwnerItems(1, 1, 1);
        }

        @Test
        void getAllByItemsOwner_whenStateIsWaitingOrRejected_thenFindAllByItemIdAndStatus() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            bookingService.getAllByItemsOwner(1, "WAITING", 1, 1);
            verify(bookingRepository, Mockito.atLeast(1)).findAllBookingsByOwnerItemsAndStatus(1, "WAITING", 1, 1);
        }


        @Test
        void getAllByItemsOwner_whenStateIsInvalid_thenUnsupportedStatusExceptionThrown() {
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            assertThrows(UnsupportedStatusException.class, () -> bookingService.getAllByItemsOwner(1, "ABC", 1, 1));
        }
    }

    @Nested
    class BookingServiceUpdateBookingStatusTests {
        @Test
        void updateBookingStatus_whenBookingIsNotFound_thenEntityNotFoundExceptionThrown() {
            when(bookingRepository.findById(1)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> bookingService.updateBookingStatus(1, 1, true));
        }

        @Test
        void updateBookingStatus_whenUserIsNotFound_thenEntityNotFoundExceptionThrown() {
            when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
            when(userRepository.findById(1)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> bookingService.updateBookingStatus(1, 1, true));
        }

        @Test
        void updateBookingStatus_whenBookerNotEqualsItemsOwner_thenWrongOwnerOrBookerExceptionThrown() {
            when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
            when(userRepository.findById(2)).thenReturn(Optional.of(anotherUser));
            assertThrows(WrongOwnerOrBookerException.class, () -> bookingService.updateBookingStatus(2, 1, true));
        }


        @Test
        void updateBookingStatus_whenBookingStatusApprovedOrRejected_thenBookingStatusAlreadyAcceptedOrRejectedExceptionThrown() {
            booking.setStatus(BookingStatus.REJECTED);
            when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            assertThrows(BookingStatusAlreadyAcceptedOrRejectedException.class, () -> bookingService.updateBookingStatus(1, 1, true));
        }

        @Test
        void updateBookingStatus_whenBookingIsFound_thenUpdateOnlyAvailableFields() {
            when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
            when(userRepository.findById(1)).thenReturn(Optional.of(user));
            Booking updatedBooking = Booking.builder().id(booking.getId()).start(booking.getStart()).end(booking.getEnd())
                    .item(booking.getItem()).booker(booking.getBooker()).status(BookingStatus.APPROVED).build();
            when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);
            BookingResponseDto savedBooking = bookingService.updateBookingStatus(1, 1, true);
            verify(bookingRepository, Mockito.times(1)).save(any(Booking.class));
            assertEquals(booking.getId(), savedBooking.getId());
            assertEquals(booking.getStart(), savedBooking.getStart());
            assertEquals(booking.getEnd(), savedBooking.getEnd());
            assertEquals(booking.getItem().getId(), savedBooking.getItem().getId());
            assertEquals(booking.getBooker().getId(), savedBooking.getBooker().getId());
            assertEquals(BookingStatus.APPROVED.name(), savedBooking.getStatus());
        }


    }

}
