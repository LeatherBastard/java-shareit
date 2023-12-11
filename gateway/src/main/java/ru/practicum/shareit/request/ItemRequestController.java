package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {


    private static final String LOGGER_GET_ITEM_REQUESTS_BY_USER_MESSAGE = "Returning item requests by user";
    private static final String LOGGER_GET_ITEM_REQUESTS_BY_ALL_USERS_MESSAGE = "Returning item requests by all users";
    private static final String LOGGER_ADD_ITEM_REQUEST_MESSAGE = "Adding item request";
    private static final String LOGGER_GET_ITEM_REQUEST_BY_ID_MESSAGE = "Getting item request with id: {}";

    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @Valid @RequestBody ItemRequestRequestDto itemRequestRequestDto) {
        log.info(LOGGER_ADD_ITEM_REQUEST_MESSAGE);
        return itemRequestClient.addItemRequest(userId, itemRequestRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        log.info(LOGGER_GET_ITEM_REQUESTS_BY_USER_MESSAGE);
        return itemRequestClient.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllUsersItemRequests(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info(LOGGER_GET_ITEM_REQUESTS_BY_ALL_USERS_MESSAGE);
        return itemRequestClient.getAllUsersItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @PathVariable int requestId) {
        log.info(LOGGER_GET_ITEM_REQUEST_BY_ID_MESSAGE, requestId);
        return itemRequestClient.getItemRequest(userId, requestId);
    }

}
