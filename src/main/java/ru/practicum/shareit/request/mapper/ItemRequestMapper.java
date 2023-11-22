package ru.practicum.shareit.request.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemRequestMapper {
    public ItemRequestResponseDto mapToItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequest mapToItemRequest(ItemRequestRequestDto itemRequestRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestRequestDto.getDescription())
                .build();
    }

    public List<ItemRequestResponseDto> mapToItemDto(Iterable<ItemRequest> itemRequests) {
        List<ItemRequestResponseDto> dtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            dtos.add(mapToItemRequestDto(itemRequest));
        }
        return dtos;
    }
}
