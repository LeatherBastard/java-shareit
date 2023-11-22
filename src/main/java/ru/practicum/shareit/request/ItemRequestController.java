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

    //@TODO Добавить логер в методы

    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto addItemRequest(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @Valid @RequestBody ItemRequestRequestDto itemRequestRequestDto) {
        return itemRequestService.addItemRequest(userId, itemRequestRequestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getUserItemRequests(@RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        return itemRequestService.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllUsersItemRequests(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "20") int size) {
        return itemRequestService.getAllUsersItemRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getItemRequest(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @PathVariable int requestId) {
        return itemRequestService.getItemRequest(userId, requestId);
    }

}
