package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.service.UserServiceImpl.USER_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private static final String ITEM_NOT_FOUND_MESSAGE = "Item with id %d not found";
    private static final String WRONG_OWNER_MESSAGE = "You are not an owner ot this item!";

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;


    @Override
    public List<ItemDto> getAll() {
        return itemRepository.findAll().stream().map(itemMapper::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllByOwner(int ownerId) {
        if (userRepository.existsById(ownerId)) {
            return itemRepository.findAllByOwner(userRepository.findById(ownerId).get()).stream().map(itemMapper::mapToItemDto).collect(Collectors.toList());
        }
        throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, ownerId);

    }

    @Override
    public List<ItemDto> getAllByText(String text) {
        if (text.isEmpty())
            return new ArrayList<>();
        return itemRepository.findAllByText(text).stream().map(itemMapper::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Integer id) {
        return itemMapper.mapToItemDto
                (
                        itemRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(ITEM_NOT_FOUND_MESSAGE, id))
                );
    }

    @Override
    public ItemDto add(int ownerId, ItemDto itemDto) {
        if (userRepository.existsById(ownerId)) {
            Item item = itemMapper.mapToItem(itemDto);
            item.setOwner(userRepository.findById(ownerId).get());
            return itemMapper.mapToItemDto(itemRepository.save(item));
        }
        throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, ownerId);
    }

    @Override
    public ItemDto update(int ownerId, int itemId, ItemDto item) {
        if (itemRepository.existsById(itemId)) {
            Item oldItem = itemRepository.findById(itemId).get();
            if (oldItem.getOwner().getId() != ownerId) {
                throw new WrongOwnerException(WRONG_OWNER_MESSAGE);
            }

            if (item.getName() != null)
                oldItem.setName(item.getName());
            if (item.getDescription() != null)
                oldItem.setDescription(item.getDescription());
            if (item.getAvailable() != null)
                oldItem.setAvailable(item.getAvailable());
            return itemMapper.mapToItemDto(itemRepository.save(oldItem));
        }
        throw new EntityNotFoundException(ITEM_NOT_FOUND_MESSAGE, itemId);
    }

}
