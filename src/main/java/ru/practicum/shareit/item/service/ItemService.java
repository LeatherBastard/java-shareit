package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    List<ItemRequestDto> getAll();

    List<ItemResponseDto> getAllByOwner(int ownerId, int from, int size);

    List<ItemRequestDto> getAllByText(String text, int from, int size);

    ItemResponseDto getById(int userId, int id);

    ItemRequestDto add(int ownerId, ItemRequestDto item);

    CommentResponseDto addComment(int userId, int itemId, CommentRequestDto commentRequestDto);

    ItemRequestDto update(int ownerId, int itemId, ItemRequestDto item);
}
