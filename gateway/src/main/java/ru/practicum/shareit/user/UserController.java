package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private static final String LOGGER_GET_USERS_MESSAGE = "Returning list of users";
    private static final String LOGGER_ADD_USER_MESSAGE = "Adding user ";
    private static final String LOGGER_GET_USER_BY_ID_MESSAGE = "Getting user with id: {}";
    private static final String LOGGER_UPDATE_USER_MESSAGE = "Updating user with id: {}";
    private static final String LOGGER_REMOVE_USER_MESSAGE = "Removing user with id: {}";

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserRequestDto user) {
        log.info(LOGGER_ADD_USER_MESSAGE);
        return userClient.addUser(user);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info(LOGGER_GET_USERS_MESSAGE);
        return userClient.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") int userId) {
        log.info(LOGGER_GET_USER_BY_ID_MESSAGE, userId);
        return userClient.getUser(userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") int userId, @RequestBody UserRequestDto user) {
        log.info(LOGGER_UPDATE_USER_MESSAGE, userId);
        return userClient.updateUser(userId, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") int userId) {
        log.info(LOGGER_REMOVE_USER_MESSAGE, userId);
        return userClient.deleteUser(userId);
    }

}
