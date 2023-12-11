package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


@RestController
@Slf4j
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private static final String LOGGER_GET_USERS_MESSAGE = "Returning list of users";
    private static final String LOGGER_ADD_USER_MESSAGE = "Adding user ";
    private static final String LOGGER_GET_USER_BY_ID_MESSAGE = "Getting user with id: {}";
    private static final String LOGGER_UPDATE_USER_MESSAGE = "Updating user with id: {}";
    private static final String LOGGER_REMOVE_USER_MESSAGE = "Removing user with id: {}";

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody UserDto user) {
        log.info(LOGGER_ADD_USER_MESSAGE);
        return userService.add(user);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info(LOGGER_GET_USERS_MESSAGE);
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") int userId) {
        log.info(LOGGER_GET_USER_BY_ID_MESSAGE, userId);
        return userService.getById(userId);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") int userId, @RequestBody UserDto user) {
        log.info(LOGGER_UPDATE_USER_MESSAGE, userId);
        return userService.update(userId, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") int userId) {
        log.info(LOGGER_REMOVE_USER_MESSAGE, userId);
        userService.remove(userId);
    }
}
