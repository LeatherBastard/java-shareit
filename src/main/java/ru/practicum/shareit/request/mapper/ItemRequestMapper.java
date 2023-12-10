package ru.practicum.shareit.request.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;


@Component
public class ItemRequestMapper {
    public ItemRequestResponseDto mapToItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequestRequestDto mapToItemRequestRequestDto(ItemRequest itemRequest) {
        return new ItemRequestRequestDto(itemRequest.getDescription());
    }

    public ItemRequest mapToItemRequest(ItemRequestRequestDto itemRequestRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestRequestDto.getDescription())
                .build();
    }

}
