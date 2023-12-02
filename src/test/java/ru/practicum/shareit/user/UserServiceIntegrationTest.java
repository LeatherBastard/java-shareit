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

        UserDto savedUser = userService.add(new UserDto(1, "Mark", "kostrykinmark@gmail.com"));
        UserDto foundUser = userService.getById(savedUser.getId());
        assertEquals(savedUser.getName(), foundUser.getName());
        assertEquals(savedUser.getEmail(), foundUser.getEmail());
    }

    @Test
    void getById_whenUserNotFound_thenEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> userService.getById(2));
    }
}
