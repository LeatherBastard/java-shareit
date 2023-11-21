package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto addItemRequest(int userId, ItemRequestDto itemRequestDto);

    List<ItemRequestResponseDto> getUserItemRequests(int userId);

    List<ItemRequestResponseDto> getAllUsersItemRequest(int from, int size);

    ItemRequestDto getItemRequest(int requestId);

}
