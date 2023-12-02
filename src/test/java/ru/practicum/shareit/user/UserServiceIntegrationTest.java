package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {

    private final UserService userService;

    @Test
    void getById() {
        UserDto userDto = new UserDto(1, "Mark", "kostrykinmark@gmail.com");
        UserDto savedUser = userService.add(userDto);
        assertEquals(userDto, userService.getById(savedUser.getId()));
    }

    @Test
    void getById_whenUserNotFound_thenEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> userService.getById(2));
    }
}
