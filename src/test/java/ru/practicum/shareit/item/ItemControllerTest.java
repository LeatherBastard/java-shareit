package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
 class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";

    @SneakyThrows
    @Test
    void addItem() {
        Integer userId = 1;
        ItemRequestDto itemRequestDto = ItemRequestDto
                .builder().id(1).name("Пылесос").description("Пылесос").available(true).build();
        when(itemService.add(1, itemRequestDto)).thenReturn(itemRequestDto);
        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemRequestDto.getName()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemRequestDto.getAvailable()));
        verify(itemService, Mockito.times(1)).add(userId, itemRequestDto);
    }

    @SneakyThrows
    @Test
    void addItem_whenItemNotValid_thenReturnBadRequest() {
        Integer userId = 1;
        ItemRequestDto itemRequestDto = ItemRequestDto
                .builder().id(1).name("Пылесос").description("").available(true).build();
        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());
        verify(itemService, Mockito.never()).add(userId, itemRequestDto);
    }

    @SneakyThrows
    @Test
    void addComment() {
        Integer userId = 1;
        Integer itemId = 1;
        CommentRequestDto commentRequestDto = CommentRequestDto.builder().text("Очень хороший пылесос").build();
        CommentResponseDto commentResponseDto = CommentResponseDto
                .builder().id(1).text("Очень хороший пылесос").authorName("LeatherBastard").created(LocalDateTime.now())
                .build();
        when(itemService.addComment(userId, itemId, commentRequestDto)).thenReturn(commentResponseDto);
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType("application/json")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentResponseDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentResponseDto.getAuthorName()));
        verify(itemService, Mockito.times(1)).addComment(userId, itemId, commentRequestDto);
    }

    @SneakyThrows
    @Test
    void addComment_whenCommentNotValid_thenReturnBadRequest() {
        Integer userId = 1;
        Integer itemId = 1;
        CommentRequestDto commentRequestDto = CommentRequestDto.builder().text("").build();
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType("application/json")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isBadRequest());
        verify(itemService, Mockito.never()).addComment(userId, itemId, commentRequestDto);
    }

    @SneakyThrows
    @Test
    void getAllByOwner() {
        Integer userId = 1;
        int from = 0;
        int size = 2;
        List<ItemResponseDto> itemRequests = List.of(
                ItemResponseDto
                        .builder().id(1).name("Пылесос").description("Пылесос").available(true).build(),
                ItemResponseDto
                        .builder().id(2).name("Кофеварка").description("Пылесос").available(true).build()
        );
        when(itemService.getAllByOwner(userId, from, size)).thenReturn(itemRequests);
        mockMvc.perform(get("/items")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .param("size", Integer.toString(size))
                        .param("from", Integer.toString(from)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        verify(itemService, Mockito.times(1)).getAllByOwner(userId, from, size);
    }

    @SneakyThrows
    @Test
    void getAllByText() {
        Integer userId = 1;
        int from = 0;
        int size = 2;
        String text = "кофеварка";
        List<ItemRequestDto> itemRequests = List.of(
                ItemRequestDto
                        .builder().id(1).name("Пылесос").description("Пылесос").available(true).build(),
                ItemRequestDto
                        .builder().id(2).name("Кофеварка").description("Пылесос").available(true).build()
        );
        when(itemService.getAllByText(text, from, size)).thenReturn(itemRequests);
        mockMvc.perform(get("/items/search")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .param("text", text)
                        .param("size", Integer.toString(size))
                        .param("from", Integer.toString(from)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        verify(itemService, Mockito.times(1)).getAllByText(text, from, size);
    }

    @SneakyThrows
    @Test
    void getItem() {
        Integer userId = 1;
        Integer itemId = 1;
        ItemResponseDto itemResponseDto = ItemResponseDto
                .builder().id(1).name("Пылесос").description("Пылесос").available(true).build();
        when(itemService.getById(userId, itemId)).thenReturn(itemResponseDto);
        mockMvc.perform(get("/items/{id}", itemId)
                        .header(USER_ID_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemResponseDto.getName()))
                .andExpect(jsonPath("$.description").value(itemResponseDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemResponseDto.getAvailable()));
        verify(itemService, Mockito.times(1)).getById(userId, itemId);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        Integer userId = 1;
        Integer itemId = 1;
        ItemRequestDto itemRequestDto = ItemRequestDto
                .builder().id(1).name("Пылесос").description("Пылесос").available(true).build();
        when(itemService.update(userId, itemId, itemRequestDto)).thenReturn(itemRequestDto);
        mockMvc.perform(patch("/items/{id}", itemId)
                        .contentType("application/json")
                        .header(USER_ID_REQUEST_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemRequestDto.getName()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemRequestDto.getAvailable()));
        verify(itemService, Mockito.times(1)).update(userId, itemId, itemRequestDto);
    }

}
