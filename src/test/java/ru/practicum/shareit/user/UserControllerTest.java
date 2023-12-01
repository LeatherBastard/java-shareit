package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void addUser() {
        UserDto userDto = new UserDto(1, "Mark", "kostrykinmark@gmail.com");
        when(userService.add(userDto)).thenReturn(userDto);
        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void getUser() {
        Integer userId = 1;
        UserDto userDto = new UserDto(1, "Mark", "kostrykinmark@gmail.com");
        when(userService.getById(userId)).thenReturn(userDto);
        String result = mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, Mockito.times(1)).getById(userId);
        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
        verify(userService).getAll();
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        Integer userId = 1;
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());
        verify(userService).remove(userId);
    }


}
