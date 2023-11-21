package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static ru.practicum.shareit.user.service.UserServiceImpl.USER_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    public static final String ITEM_REQUEST_NOT_FOUND_MESSAGE = "Item request with id %d not found";

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;


    public ItemRequestResponseDto addItemRequest(int userId, ItemRequestDto itemRequestDto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, userId);
        User userById = optionalUser.get();
        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(itemRequestDto);
        itemRequest.setRequestor(userById);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestMapper.mapToItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    public ItemRequestResponseDto getItemRequest(int requestId) {
        Optional<ItemRequest> optionalItemRequest = itemRequestRepository.findById(requestId);
        if (optionalItemRequest.isEmpty())
            throw new EntityNotFoundException(ITEM_REQUEST_NOT_FOUND_MESSAGE, requestId);
        return itemRequestMapper.mapToItemRequestDto(optionalItemRequest.get());
    }

}
