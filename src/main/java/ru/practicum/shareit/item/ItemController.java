package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String LOGGER_GET_ITEMS_BY_OWNER_MESSAGE = "Returning items by owner";
    private static final String LOGGER_GET_ITEMS_BY_TEXT_MESSAGE = "Returning items by text";
    private static final String LOGGER_ADD_ITEM_MESSAGE = "Adding item";
    private static final String LOGGER_ADD_COMMENT_MESSAGE = "Adding comment";
    private static final String LOGGER_GET_ITEM_BY_ID_MESSAGE = "Getting item with id: {}";
    private static final String LOGGER_UPDATE_ITEM_MESSAGE = "Updating item with id: {}";

    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemResponseDto> getAllByOwner(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "20") int size) {
        log.info(LOGGER_GET_ITEMS_BY_OWNER_MESSAGE);
        return itemService.getAllByOwner(userId, from, size);
    }

    @GetMapping("search")
    public List<ItemRequestDto> getAllByText(@RequestParam String text,  @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "20") int size) {
        log.info(LOGGER_GET_ITEMS_BY_TEXT_MESSAGE);
        return itemService.getAllByText(text, from, size);
    }

    @GetMapping("/{id}")
    public ItemResponseDto getItem(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @PathVariable("id") int itemId) {
        log.info(LOGGER_GET_ITEM_BY_ID_MESSAGE, itemId);
        return itemService.getById(userId, itemId);
    }

    @PostMapping
    public ItemRequestDto addItem(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info(LOGGER_ADD_ITEM_MESSAGE);
        return itemService.add(userId, itemRequestDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @PathVariable int itemId, @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info(LOGGER_ADD_COMMENT_MESSAGE);
        return itemService.addComment(userId, itemId, commentRequestDto);

    }

    @PatchMapping("/{id}")
    public ItemRequestDto updateItem(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @PathVariable("id") int itemId, @RequestBody ItemRequestDto itemRequestDto) {
        log.info(LOGGER_UPDATE_ITEM_MESSAGE, itemId);
        return itemService.update(userId, itemId, itemRequestDto);
    }

}
