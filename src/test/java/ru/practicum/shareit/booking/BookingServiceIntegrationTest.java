package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;

    private final BookingService bookingService;

    @Test
    void getAllByBooker() {
        UserDto firstUser = userService.add(new UserDto(1, "Mark", "kostrykinmark@gmail.com"));
        UserDto secondUser = userService.add(new UserDto(2, "John", "johndoe@gmail.com"));


        ItemRequestDto firstItem = itemService.add(firstUser.getId(), ItemRequestDto
                .builder().name("Пылесос").description("Пылесос").available(true).build());

        ItemRequestDto secondItem = itemService.add(firstUser.getId(), ItemRequestDto
                .builder().name("Кофеварка").description("Кофеварка").available(true).build());

        BookingResponseDto firstBooking = bookingService.add(secondUser.getId(), BookingRequestDto
                .builder()
                .start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusMonths(1)).itemId(firstItem.getId())
                .build());
        BookingResponseDto secondBooking = bookingService.add(secondUser.getId(), BookingRequestDto
                .builder()
                .start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusMonths(1)).itemId(secondItem.getId())
                .build());


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
