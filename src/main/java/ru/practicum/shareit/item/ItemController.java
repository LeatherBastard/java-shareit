package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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
    private static final String LOGGER_GET_ITEM_BY_ID_MESSAGE = "Getting item with id: {}";
    private static final String LOGGER_UPDATE_ITEM_MESSAGE = "Updating item with id: {}";

    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        log.info(LOGGER_GET_ITEMS_BY_OWNER_MESSAGE);
        return itemService.getAllByOwner(userId);
    }

    @GetMapping("search")
    public List<ItemDto> getAllByText(@RequestParam String text) {
        log.info(LOGGER_GET_ITEMS_BY_TEXT_MESSAGE);
        return itemService.getAllByText(text);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable("id") int itemId) {
        log.info(LOGGER_GET_ITEM_BY_ID_MESSAGE, itemId);
        return itemService.getById(itemId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @Valid @RequestBody ItemDto itemDto) {
        log.info(LOGGER_ADD_ITEM_MESSAGE);
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader(USER_ID_REQUEST_HEADER) int userId, @PathVariable("id") int itemId, @RequestBody ItemDto itemDto) {
        log.info(LOGGER_UPDATE_ITEM_MESSAGE, itemId);
        return itemService.update(userId, itemId, itemDto);
    }

}
