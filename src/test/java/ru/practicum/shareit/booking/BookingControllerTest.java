package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
 class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";


    @SneakyThrows
    @Test
    void addBookingRequest() {
        Integer userId = 1;
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .id(1).start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(1)).itemId(1)
                .build();
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(1).start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(1)).itemId(1).status("APPROVED")
                .build();
        when(bookingService.add(1, bookingRequestDto)).thenReturn(bookingResponseDto);
        String result = mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(bookingService, Mockito.times(1)).add(userId, bookingRequestDto);
        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @SneakyThrows
    @Test
    void addBooking_whenBookingNotValid_thenReturnBadRequest() {
        Integer userId = 1;
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .id(1).start(null).end(LocalDateTime.now().plusDays(1)).itemId(1)
                .build();
        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());
        verify(bookingService, Mockito.never()).add(userId, bookingRequestDto);
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        Integer userId = 1;
        Integer bookingId = 1;
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(1).start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(1)).itemId(1).status("APPROVED")
                .build();
        when(bookingService.getById(userId, bookingId)).thenReturn(bookingResponseDto);
        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(bookingService, Mockito.times(1)).getById(userId, bookingId);
        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @SneakyThrows
    @Test
    void getUserBookings() {
        Integer userId = 1;
        int from = 0;
        int size = 2;
        String state = "ALL";
        List<BookingResponseDto> bookings = List.of(
                BookingResponseDto.builder()
                        .id(1).start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(1)).itemId(1).status("REJECTED")
                        .build(),
                BookingResponseDto.builder()
                        .id(1).start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(1)).itemId(2).status("APPROVED")
                        .build()
        );
        when(bookingService.getAllByBooker(userId, state, from, size)).thenReturn(bookings);
        mockMvc.perform(get("/bookings")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .param("state", state)
                        .param("size", Integer.toString(size))
                        .param("from", Integer.toString(from)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        verify(bookingService, Mockito.times(1)).getAllByBooker(userId, state, from, size);
    }

    @SneakyThrows
    @Test
    void getBookingsByItemsOwner() {
        Integer userId = 1;
        int from = 0;
        int size = 2;
        String state = "ALL";
        List<BookingResponseDto> bookings = List.of(
                BookingResponseDto.builder()
                        .id(1).start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(1)).itemId(1).status("REJECTED")
                        .build(),
                BookingResponseDto.builder()
                        .id(1).start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(1)).itemId(2).status("APPROVED")
                        .build()
        );
        when(bookingService.getAllByItemsOwner(userId, state, from, size)).thenReturn(bookings);
        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .param("state", state)
                        .param("size", Integer.toString(size))
                        .param("from", Integer.toString(from)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        verify(bookingService, Mockito.times(1)).getAllByItemsOwner(userId, state, from, size);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        Integer userId = 1;
        Integer bookingId = 1;
        boolean approved = true;
        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(1).start(LocalDateTime.now().plusHours(1)).end(LocalDateTime.now().plusDays(1)).itemId(1).status("APPROVED")
                .build();
        when(bookingService.updateBookingStatus(userId, bookingId, approved)).thenReturn(bookingResponseDto);
        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .contentType("application/json")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .content(objectMapper.writeValueAsString(bookingResponseDto))
                        .param("approved", Boolean.toString(approved)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(bookingService, Mockito.times(1)).updateBookingStatus(userId, bookingId, approved);
        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }
}
