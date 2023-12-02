package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
class BookingRepositoryTest {

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

    private Booking thirdBooking;

    private Booking fourthBooking;

    @BeforeEach
    void initialize() {
        firstUser = userRepository.save(User.builder().name("Mark").email("kostrykinmark@gmail.com").build());
        secondUser = userRepository.save(User.builder().name("John").email("johndoe@gmail.com").build());
        thirdUser = userRepository.save(User.builder().name("Jane").email("janedoe@gmail.com").build());

        firstItem = itemRepository.save(Item.builder().name("Пылесос").description("Пылесос").owner(firstUser).available(true).build());
        secondItem = itemRepository.save(Item.builder().name("Кофеварка").description("Кофеварка").owner(firstUser).available(true).build());

        firstBooking = bookingRepository.save(Booking
                .builder()
                .start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(2)).item(firstItem).booker(secondUser).status(BookingStatus.APPROVED)
                .build());

        secondBooking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().minusHours(2)).item(firstItem).booker(thirdUser).status(BookingStatus.APPROVED)
                .build());

        thirdBooking = bookingRepository.save(Booking
                .builder()
                .start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).item(secondItem).booker(secondUser).status(BookingStatus.APPROVED)
                .build());

        fourthBooking = bookingRepository.save(Booking
                .builder()
                .start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().plusDays(2)).item(secondItem).booker(thirdUser).status(BookingStatus.WAITING)
                .build());

    }

    @Test
    void findLastBookingDateForItem() {
        Optional<Booking> booking = bookingRepository.findLastBookingDateForItem(firstItem.getId());
        assertTrue(booking.isPresent());
        assertEquals(secondBooking, booking.get());
    }

    @Test
    void findNextBookingDateForItem() {
        Optional<Booking> booking = bookingRepository.findNextBookingDateForItem(firstItem.getId());
        assertTrue(booking.isPresent());
        assertEquals(firstBooking, booking.get());
    }

    @Test
    void findAllByUser() {
        List<Booking> bookings = bookingRepository.findAllByUser(secondUser.getId(), 0, 3);
        assertEquals(2, bookings.size());
        assertEquals(thirdBooking, bookings.get(0));
        assertEquals(firstBooking, bookings.get(1));
    }

    @Test
    void findAllByUserAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByUserAndStatus(secondUser.getId(), BookingStatus.APPROVED.name(), 0, 3);
        assertEquals(2, bookings.size());
        assertEquals(thirdBooking, bookings.get(0));
        assertEquals(firstBooking, bookings.get(1));
    }

    @Test
    void findAllByItemId() {
        List<Booking> bookings = bookingRepository.findAllByItemId(firstItem.getId(), 0, 2);
        assertEquals(2, bookings.size());
        assertEquals(firstBooking, bookings.get(0));
        assertEquals(secondBooking, bookings.get(1));
    }

    @Test
    void findAllByItemIdAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatus(secondItem.getId(), "APPROVED", 0, 2);
        assertEquals(1, bookings.size());
        assertEquals(thirdBooking, bookings.get(0));
    }

    @Test
    void findAllFutureBookingsByUser() {
        List<Booking> bookings = bookingRepository.findAllFutureBookingsByUser(secondUser.getId(), 0, 2);
        assertEquals(2, bookings.size());
        assertEquals(thirdBooking, bookings.get(0));
        assertEquals(firstBooking, bookings.get(1));
    }

    @Test
    void findAllCurrentBookingsByUser() {
        List<Booking> bookings = bookingRepository.findAllCurrentBookingsByUser(thirdUser.getId(), 0, 2);
        assertEquals(1, bookings.size());
        assertEquals(fourthBooking, bookings.get(0));
    }

    @Test
    void findAllPastBookingsByUser() {
        List<Booking> bookings = bookingRepository.findAllPastBookingsByUser(thirdUser.getId(), 0, 1);
        assertEquals(1, bookings.size());
        assertEquals(secondBooking, bookings.get(0));
    }

    @Test
    void findAllFutureBookingsByItem() {
        List<Booking> bookings = bookingRepository.findAllFutureBookingsByItem(secondItem.getId(), 0, 2);
        assertEquals(1, bookings.size());
        assertEquals(thirdBooking, bookings.get(0));
    }

    @Test
    void findAllCurrentBookingsByItem() {
        List<Booking> bookings = bookingRepository.findAllCurrentBookingsByItem(secondItem.getId(), 0, 2);
        assertEquals(1, bookings.size());
        assertEquals(fourthBooking, bookings.get(0));
    }

    @Test
    void findAllPastBookingsByItem() {
        List<Booking> bookings = bookingRepository.findAllPastBookingsByItem(firstItem.getId(), 0, 3);
        assertEquals(1, bookings.size());
        assertEquals(secondBooking, bookings.get(0));
    }


}
