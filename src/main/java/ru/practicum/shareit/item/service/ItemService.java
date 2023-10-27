package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAll();

    List<ItemDto> getAllByOwner(int ownerId);

    List<ItemDto> getAllByText(String text);

    ItemDto getById(Integer id);

    ItemDto add(int ownerId, ItemDto item);

    ItemDto update(int ownerId, int itemId, ItemDto item);
}
