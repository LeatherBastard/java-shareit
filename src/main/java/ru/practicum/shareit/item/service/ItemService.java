package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.List;

public interface ItemService {
    List<ItemRequestDto> getAll();

    List<ItemResponseDto> getAllByOwner(int ownerId);

    List<ItemRequestDto> getAllByText(String text);

    ItemResponseDto getById(int userId, int id);

    ItemRequestDto add(int ownerId, ItemRequestDto item);

    CommentResponseDto addComment(int userId, int itemId, CommentRequestDto commentRequestDto);

    ItemRequestDto update(int ownerId, int itemId, ItemRequestDto item);
}
