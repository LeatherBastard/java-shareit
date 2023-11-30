package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BookingRepository bookingRepository;

    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private Item firstItem;
    private Item secondItem;

    private Booking firstBooking;
    private Booking secondBooking;

    @BeforeEach
    void initialize() {
        User firstUser = User.builder().name("Mark").email("kostrykinmark@gmail.com").build();
        userRepository.save(firstUser);
        User secondUser = User.builder().name("John").email("johndoe@gmail.com").build();
        userRepository.save(secondUser);
        User thirdUser = User.builder().name("Jane").email("janedoe@gmail.com").build();
        userRepository.save(thirdUser);
        firstItem = Item.builder().name("Пылесос").description("Пылесос").owner(firstUser).available(true).build();
        itemRepository.save(firstItem);
        secondItem = Item.builder().name("Кофеварка").description("Кофеварка").owner(firstUser).available(true).build();
        itemRepository.save(secondItem);

        Booking firstBooking = Booking
                .builder()
                .start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(2)).item(firstItem).booker(secondUser).status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(firstBooking);
        Booking secondBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().minusHours(2)).item(firstItem).booker(thirdUser).status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(secondBooking);

        Booking thirdBooking = Booking
                .builder()
                .start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).item(secondItem).booker(secondUser).status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(thirdBooking);

        Booking fourthBooking = Booking
                .builder()
                .start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().plusDays(2)).item(secondItem).booker(thirdUser).status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(fourthBooking);
    }

    @Test
    void findLastBookingDateForItem() {
        Optional<Booking> booking = bookingRepository.findLastBookingDateForItem(1);
        assertTrue(booking.isPresent());
        assertEquals(booking.get().getBooker().getId(), 3);
    }

    @Test
    void findNextBookingDateForItem() {
        Optional<Booking> booking = bookingRepository.findNextBookingDateForItem(1);
        assertTrue(booking.isPresent());
        assertEquals(booking.get().getBooker().getId(), 2);
    }

    @Test
    void findAllByUser() {
        List<Booking> bookings = bookingRepository.findAllByUser(2, 0, 3);
        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByUserAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByUserAndStatus(2, BookingStatus.APPROVED.name(), 0, 3);
        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByItemId() {
        List<Booking> bookings = bookingRepository.findAllByItemId(1, 0, 2);
        assertEquals(2, bookings.size());
        assertEquals(4, bookings.get(0).getBooker().getId());
        assertEquals(5, bookings.get(1).getBooker().getId());
    }

    @Test
    void findAllByItemIdAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatus(2, "APPROVED", 0, 2);
        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getBooker().getId(), 2);
    }

    @Test
    void findAllFutureBookingsByUser() {
        List<Booking> bookings = bookingRepository.findAllFutureBookingsByUser(2, 0, 2);
        assertEquals(2, bookings.size());
        assertEquals(3, bookings.get(0).getId());
        assertEquals(1, bookings.get(1).getId());
    }

    @Test
    void findAllCurrentBookingsByUser() {
        List<Booking> bookings = bookingRepository.findAllCurrentBookingsByUser(3, 0, 2);
        assertEquals(1, bookings.size());
        assertEquals(2, bookings.get(0).getItem().getId());
        assertEquals(3, bookings.get(0).getBooker().getId());
        assertEquals(BookingStatus.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void findAllPastBookingsByUser() {
        List<Booking> bookings = bookingRepository.findAllPastBookingsByUser(3, 0, 1);
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getItem().getId());
        assertEquals(3, bookings.get(0).getBooker().getId());
        assertEquals(BookingStatus.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void findAllFutureBookingsByItem() {
        List<Booking> bookings = bookingRepository.findAllFutureBookingsByItem(2, 0, 2);
        assertEquals(1, bookings.size());
        assertEquals(2, bookings.get(0).getItem().getId());
        assertEquals(2, bookings.get(0).getBooker().getId());
        assertEquals(BookingStatus.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void findAllCurrentBookingsByItem() {
        List<Booking> bookings = bookingRepository.findAllCurrentBookingsByItem(2, 0, 2);
        assertEquals(1, bookings.size());
        assertEquals(2, bookings.get(0).getItem().getId());
        assertEquals(3, bookings.get(0).getBooker().getId());
        assertEquals(BookingStatus.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void findAllPastBookingsByItem() {
        List<Booking> bookings = bookingRepository.findAllPastBookingsByItem(1, 0, 3);
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getItem().getId());
        assertEquals(3, bookings.get(0).getBooker().getId());
        assertEquals(BookingStatus.APPROVED, bookings.get(0).getStatus());
    }


}
