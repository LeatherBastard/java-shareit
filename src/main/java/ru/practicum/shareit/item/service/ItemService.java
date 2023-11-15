package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentView;
import ru.practicum.shareit.item.dto.ItemBookingDatesView;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAll();

    List<ItemBookingDatesView> getAllByOwner(Integer ownerId);

    List<ItemDto> getAllByText(String text);

    ItemBookingDatesView getById(Integer userId, Integer id);

    ItemDto add(Integer ownerId, ItemDto item);

    CommentView addComment(Integer userId, Integer itemId, CommentDto commentDto);

    ItemDto update(Integer ownerId, Integer itemId, ItemDto item);
}
