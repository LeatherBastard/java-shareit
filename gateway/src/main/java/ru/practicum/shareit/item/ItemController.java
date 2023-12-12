package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.PaginationBoundariesException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.validator.AddItemValidator;
import ru.practicum.shareit.item.validator.UpdateItemValidator;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String LOGGER_GET_ITEMS_BY_OWNER_MESSAGE = "Returning items by owner";
    private static final String LOGGER_GET_ITEMS_BY_TEXT_MESSAGE = "Returning items by text: {}";
    private static final String LOGGER_ADD_ITEM_MESSAGE = "Adding item";
    private static final String LOGGER_ADD_COMMENT_MESSAGE = "Adding comment";
    private static final String LOGGER_GET_ITEM_BY_ID_MESSAGE = "Getting item with id: {}";
    private static final String LOGGER_UPDATE_ITEM_MESSAGE = "Updating item with id: {}";

    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @Validated(AddItemValidator.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info(LOGGER_ADD_ITEM_MESSAGE);
        return itemClient.addItem(userId, itemRequestDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @PathVariable int itemId, @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info(LOGGER_ADD_COMMENT_MESSAGE);
        return itemClient.addComment(userId, itemId, commentRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info(LOGGER_GET_ITEMS_BY_OWNER_MESSAGE);
        return itemClient.getAllByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAllByText(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @RequestParam String text, @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        if (from < 0 || size <= 0) {
            throw new PaginationBoundariesException(from, size);
        }
        log.info(LOGGER_GET_ITEMS_BY_TEXT_MESSAGE, text);
        return itemClient.getAllByText(userId, text, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @PathVariable("id") int itemId) {
        log.info(LOGGER_GET_ITEM_BY_ID_MESSAGE, itemId);
        return itemClient.getItem(userId, itemId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @PathVariable("id") int itemId, @Validated(UpdateItemValidator.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info(LOGGER_UPDATE_ITEM_MESSAGE, itemId);
        return itemClient.updateItem(userId, itemId, itemRequestDto);
    }

}
