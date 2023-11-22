package ru.practicum.shareit.request.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.QItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.service.UserServiceImpl.USER_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    public static final String ITEM_REQUEST_NOT_FOUND_MESSAGE = "Item request with id %d not found";

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestMapper itemRequestMapper;


    public ItemRequestResponseDto addItemRequest(int userId, ItemRequestRequestDto itemRequestRequestDto) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, userId);
        User userById = optionalUser.get();
        ItemRequest itemRequest = itemRequestMapper.mapToItemRequest(itemRequestRequestDto);
        itemRequest.setRequestor(userById);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestMapper.mapToItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    public List<ItemRequestResponseDto> getUserItemRequests(int userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, userId);
        List<ItemRequestResponseDto> itemRequests = itemRequestRepository.findAllByRequestor_Id(userId).stream()
                .map(itemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
        setItemsToItemRequests(itemRequests);
        return itemRequests;
    }

    public List<ItemRequestResponseDto> getAllUsersItemRequest(int userId, int from, int size) {
        BooleanExpression notByUserId = QItemRequest.itemRequest.requestor.id.notIn(userId);
        Sort sort = Sort.by("created").descending();
        PageRequest pageRequest = PageRequest.of(from, size, sort);
        Iterable<ItemRequest> iterableItemRequests = itemRequestRepository.findAll(notByUserId, pageRequest);
        List<ItemRequestResponseDto> itemRequests = itemRequestMapper.mapToItemDto(iterableItemRequests);
        setItemsToItemRequests(itemRequests);
        return itemRequests;

    }

    public ItemRequestResponseDto getItemRequest(int userId, int requestId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, userId);
        Optional<ItemRequest> optionalItemRequest = itemRequestRepository.findById(requestId);
        if (optionalItemRequest.isEmpty())
            throw new EntityNotFoundException(ITEM_REQUEST_NOT_FOUND_MESSAGE, requestId);

        ItemRequestResponseDto result = itemRequestMapper.mapToItemRequestDto(optionalItemRequest.get());
        result.setItems(
                itemRepository.findAllByRequest_Id(result.getId())
                        .stream()
                        .map(itemMapper::mapToItemDto)
                        .collect(Collectors.toList())
        );
        return result;
    }

    private void setItemsToItemRequests(List<ItemRequestResponseDto> itemRequests) {
        itemRequests.forEach(
                itemRequestResponseDto ->
                        itemRequestResponseDto.setItems(
                                itemRepository.findAllByRequest_Id(itemRequestResponseDto.getId())
                                        .stream()
                                        .map(itemMapper::mapToItemDto)
                                        .collect(Collectors.toList())
                        )
        );
    }

}
