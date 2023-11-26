package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
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

    private BookingMapper mapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User anotherUser;
    private Item item;

    private


    @BeforeEach
    void setUp() {
        mapper = new BookingMapper();
        bookingService = new BookingServiceImpl(userRepository, itemRepository, bookingRepository, mapper);
        user = new User(1, "Mark", "kostrykinmark@gmail.com");
        anotherUser = new User(2, "John", "johndoe@gmail.com");
        item = new Item(1, "Пылесос", "Пылесос", true, user, null);

    }

    @Nested
    class BookingServiceAddTests {
        @Test
        void add_whenBookerNotFound_thenEntityNotFoundExceptionThrown() {
            BookingRequestDto bookingRequestDto = BookingRequestDto
                    .builder().start(LocalDateTime.now()).end(LocalDateTime.now()).itemId(1).build();
            when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> bookingService.add(1, bookingRequestDto));
            verify(userRepository, Mockito.times(1)).findById(1);
            verify(bookingRepository, Mockito.never()).save(any(Booking.class));
        }

        @Test
        void add_whenItemNotFound_thenEntityNotFoundExceptionThrown() {
            BookingRequestDto bookingRequestDto = BookingRequestDto
                    .builder().start(LocalDateTime.now()).end(LocalDateTime.now()).itemId(1).build();
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
                    .builder().start(LocalDateTime.now()).end(LocalDateTime.now()).itemId(1).build();
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
                    .builder().start(LocalDateTime.now()).end(LocalDateTime.now()).itemId(1).build();
            when(userRepository.findById(anyInt()))
                    .thenReturn(Optional.of(user));
            when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
            assertThrows(BookingOwnerEqualsBookerException.class, () -> bookingService.add(1, bookingRequestDto));
            verify(userRepository, Mockito.times(1)).findById(1);
            verify(itemRepository, Mockito.times(1)).findById(1);
            verify(bookingRepository, Mockito.never()).save(any(Booking.class));
        }

        @Test
        void add_whenStartEqualsEnd_thenBookingDateValidationExceptionThrown() {
            BookingRequestDto bookingRequestDto = BookingRequestDto
                    .builder().start(LocalDateTime.now()).end(LocalDateTime.now()).itemId(1).build();
            when(userRepository.findById(anyInt()))
                    .thenReturn(Optional.of(anotherUser));
            when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
            assertThrows(BookingDateValidationException.class, () -> bookingService.add(2, bookingRequestDto));
            verify(userRepository, Mockito.times(1)).findById(2);
            verify(itemRepository, Mockito.times(1)).findById(1);
            verify(bookingRepository, Mockito.never()).save(any(Booking.class));
        }

        @Test
        void add_whenStartBeforeCurrentDate_thenBookingDateValidationExceptionThrown() {
            BookingRequestDto bookingRequestDto = BookingRequestDto
                    .builder().start(LocalDateTime.now().minusHours(2)).end(LocalDateTime.now().plusDays(2)).itemId(1).build();
            when(userRepository.findById(anyInt()))
                    .thenReturn(Optional.of(anotherUser));
            when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
            assertThrows(BookingDateValidationException.class, () -> bookingService.add(2, bookingRequestDto));
            verify(userRepository, Mockito.times(1)).findById(2);
            verify(itemRepository, Mockito.times(1)).findById(1);
            verify(bookingRepository, Mockito.never()).save(any(Booking.class));
        }

        @Test
        void add_whenEndBeforeCurrentDate_thenBookingDateValidationExceptionThrown() {
            BookingRequestDto bookingRequestDto = BookingRequestDto
                    .builder().start(LocalDateTime.now()).end(LocalDateTime.now().minusHours(2)).itemId(1).build();
            when(userRepository.findById(anyInt()))
                    .thenReturn(Optional.of(anotherUser));
            when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
            assertThrows(BookingDateValidationException.class, () -> bookingService.add(2, bookingRequestDto));
            verify(userRepository, Mockito.times(1)).findById(2);
            verify(itemRepository, Mockito.times(1)).findById(1);
            verify(bookingRepository, Mockito.never()).save(any(Booking.class));
        }

        @Test
        void add_whenEndBeforeStart_thenBookingDateValidationExceptionThrown() {
            BookingRequestDto bookingRequestDto = BookingRequestDto
                    .builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(1)).itemId(1).build();
            when(userRepository.findById(anyInt()))
                    .thenReturn(Optional.of(anotherUser));
            when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
            assertThrows(BookingDateValidationException.class, () -> bookingService.add(2, bookingRequestDto));
            verify(userRepository, Mockito.times(1)).findById(2);
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
            Booking bookingToSave = mapper.mapToBooking(bookingRequestDto);
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
            assertEquals(mapper.mapToBookingView(booking), bookingService.getById(1, 1));
        }
    }

    @Nested
    class BookingServiceGetAllByBookerTests {
        @Test
        void getAllByBooker_whenInvalidFromOrSize_thenPaginationBoundariesExceptionThrown() {
            assertThrows(PaginationBoundariesException.class,
                    () -> bookingService.getAllByBooker(1, "ALL", -1, -1));
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
           assertThrows(UnsupportedStatusException.class,()->bookingService.getAllByBooker(1,"ABC",1,1));
        }
    }
}
