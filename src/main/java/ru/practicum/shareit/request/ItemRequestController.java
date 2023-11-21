package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestResponseDto addItemRequest(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {

    }

    @GetMapping
    public List<ItemRequestResponseDto> getUserItemRequests(@RequestHeader(USER_ID_REQUEST_HEADER) int userId) {

    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllUsersItemRequests(@RequestParam int from, @RequestParam int size) {

    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getItemRequest(@PathVariable int requestId) {

    }


}
