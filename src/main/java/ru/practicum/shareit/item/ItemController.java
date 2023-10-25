package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID_REQUEST_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader(USER_ID_REQUEST_HEADER) int userId) {
        return itemService.getAllByOwner(userId);
    }

    @GetMapping
    public List<ItemDto> getAllByText(@RequestParam String text) {
        return itemService.getAllByText(text);
    }

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable("id") int itemId) {
        return itemService.getById(itemId);
    }


}
