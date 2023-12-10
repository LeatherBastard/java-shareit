package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;


@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String LOGGER_GET_ITEM_REQUESTS_BY_USER_MESSAGE = "Returning item requests by user";
    private static final String LOGGER_GET_ITEM_REQUESTS_BY_ALL_USERS_MESSAGE = "Returning item requests by all users";
    private static final String LOGGER_ADD_ITEM_REQUEST_MESSAGE = "Adding item request";
    private static final String LOGGER_GET_ITEM_REQUEST_BY_ID_MESSAGE = "Getting item request with id: {}";

    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto addItemRequest(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @Valid @RequestBody ItemRequestRequestDto itemRequestRequestDto) {
        log.info(LOGGER_ADD_ITEM_REQUEST_MESSAGE);
        return itemRequestService.addItemRequest(userId, itemRequestRequestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getUserItemRequests(@RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        log.info(LOGGER_GET_ITEM_REQUESTS_BY_USER_MESSAGE);
        return itemRequestService.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllUsersItemRequests(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "20") int size) {
        log.info(LOGGER_GET_ITEM_REQUESTS_BY_ALL_USERS_MESSAGE);
        return itemRequestService.getAllUsersItemRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getItemRequest(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @PathVariable int requestId) {
        log.info(LOGGER_GET_ITEM_REQUEST_BY_ID_MESSAGE, requestId);
        return itemRequestService.getItemRequest(userId, requestId);
    }

}
