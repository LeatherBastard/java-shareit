package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto addItemRequest(int userId, ItemRequestRequestDto itemRequestRequestDto);

    List<ItemRequestResponseDto> getUserItemRequests(int userId);

    List<ItemRequestResponseDto> getAllUsersItemRequest(int userId, int from, int size);

    ItemRequestResponseDto getItemRequest(int userId,int requestId);

}
