package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.PaginationBoundariesException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;

    private final BookingService bookingService;

    @Test
    void getAllByBooker() {
        UserDto firstUser = new UserDto(1, "Mark", "kostrykinmark@gmail.com");
        UserDto secondUser = new UserDto(2, "John", "johndoe@gmail.com");
        userService.add(firstUser);
        userService.add(secondUser);
        ItemRequestDto firstItem = ItemRequestDto
                .builder().id(1).name("Пылесос").description("Пылесос").available(true).build();
        ItemRequestDto secondItem = ItemRequestDto
                .builder().id(2).name("Кофеварка").description("Кофеварка").available(true).build();
        itemService.add(firstUser.getId(), firstItem);
        itemService.add(firstUser.getId(), secondItem);

        BookingRequestDto firstBooking = BookingRequestDto
                .builder()
                .id(1).start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusMonths(1)).itemId(1)
                .build();
        BookingRequestDto secondBooking = BookingRequestDto
                .builder()
                .id(2).start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusMonths(1)).itemId(2)
                .build();
        bookingService.add(secondUser.getId(), firstBooking);
        bookingService.add(secondUser.getId(), secondBooking);

        List<BookingResponseDto> bookings = bookingService.getAllByBooker(secondUser.getId(), "ALL", 0, 2);
        assertEquals(2, bookings.size());
        assertEquals(secondItem.getId(), bookings.get(0).getItemId());
        assertEquals(secondUser.getId(), bookings.get(0).getBooker().getId());
        assertEquals(firstItem.getId(), bookings.get(1).getItemId());
        assertEquals(secondUser.getId(), bookings.get(1).getBooker().getId());
    }

    @Test
    void getAllByBooker_whenFromOrSizeWrong_thenPaginationBoundariesException() {
        assertThrows(PaginationBoundariesException.class, () -> bookingService.getAllByBooker(1, "ALL", -1, 3));
    }


    @Test
    void getAllByBooker_whenFromOrSizeWrong_thenEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> bookingService.getAllByBooker(2, "ALL", 0, 3));
    }
}
