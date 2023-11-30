package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;


    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";

    @SneakyThrows
    @Test
    void addItemRequest() {
        Integer userId = 1;
        ItemRequestRequestDto itemRequestRequestDto = new ItemRequestRequestDto("Нужен пылесос");
        ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto(userId, "Нужен пылесос", LocalDateTime.now(), null);
        when(itemRequestService.addItemRequest(1, itemRequestRequestDto)).thenReturn(itemRequestResponseDto);
        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemRequestRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(itemRequestRequestDto.getDescription()));
        verify(itemRequestService, Mockito.times(1)).addItemRequest(1, itemRequestRequestDto);
    }

    @SneakyThrows
    @Test
    void addItemRequest_whenItemRequestNotValid_thenReturnBadRequest() {
        Integer userId = 1;
        ItemRequestRequestDto itemRequestRequestDto = new ItemRequestRequestDto("");
        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemRequestRequestDto)))
                .andExpect(status().isBadRequest());
        verify(itemRequestService, Mockito.never()).addItemRequest(1, itemRequestRequestDto);
    }

    @SneakyThrows
    @Test
    void getUserItemRequests() {
        Integer userId = 1;
        List<ItemRequestResponseDto> itemRequests = List.of(
                new ItemRequestResponseDto(userId, "Нужен пылесос", LocalDateTime.now(), null),
                new ItemRequestResponseDto(userId, "Нужна кофеварка", LocalDateTime.now(), null)
        );
        when(itemRequestService.getUserItemRequests(userId)).thenReturn(itemRequests);
        mockMvc.perform(get("/requests")
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        verify(itemRequestService, Mockito.times(1)).getUserItemRequests(userId);
    }

    @SneakyThrows
    @Test
    void getAllUsersItemRequests() {
        Integer userId = 1;
        int from = 0;
        int size = 1;
        List<ItemRequestResponseDto> itemRequests = List.of(
                new ItemRequestResponseDto(userId, "Нужен пылесос", LocalDateTime.now(), null)
        );
        when(itemRequestService.getAllUsersItemRequest(userId, from, size)).thenReturn(itemRequests);
        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .param("size", Integer.toString(size))
                        .param("from", Integer.toString(from)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(itemRequestService, Mockito.times(1)).getAllUsersItemRequest(userId, from, size);
    }

    @SneakyThrows
    @Test
    void getUser() {
        Integer userId = 1;
        Integer requestId = 1;
        ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto(requestId, "Нужен пылесос", LocalDateTime.now(), null);
        when(itemRequestService.getItemRequest(userId, requestId)).thenReturn(itemRequestResponseDto);
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestResponseDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestResponseDto.getDescription()));
        verify(itemRequestService, Mockito.times(1)).getItemRequest(userId, requestId);
    }
}
