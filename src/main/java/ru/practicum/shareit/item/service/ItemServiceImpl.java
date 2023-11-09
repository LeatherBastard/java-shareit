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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private static final String ITEM_NOT_FOUND_MESSAGE = "Item with id %d not found";
    private static final String WRONG_OWNER_MESSAGE = "You are not an owner ot this item!";

    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    @Override
    public List<ItemDto> getAll() {
        return repository.findAll().stream().map(mapper::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllByOwner(int ownerId) {
        return repository.findAllByOwner(userRepository.getById(ownerId)).stream().map(mapper::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllByText(String text) {
        return repository.findAllByText(text).stream().map(mapper::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Integer id) {
        return mapper.mapToItemDto
                (
                        repository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(ITEM_NOT_FOUND_MESSAGE, id))
                );
    }

    @Override
    public ItemDto add(int ownerId, ItemDto itemDto) {
        Item item = mapper.mapToItem(itemDto);
        item.setOwner(userRepository.getById(ownerId));
        return mapper.mapToItemDto(repository.save(item));
    }

    @Override
    public ItemDto update(int ownerId, int itemId, ItemDto item) {
        Item oldItem = mapper.mapToItem(getById(itemId));
        if (oldItem.getOwner().getId() != ownerId) {
            throw new WrongOwnerException(WRONG_OWNER_MESSAGE);
        }

        if (item.getName() != null)
            oldItem.setName(item.getName());
        if (item.getDescription() != null)
            oldItem.setDescription(item.getDescription());
        if (item.getAvailable() != null)
            oldItem.setAvailable(item.getAvailable());
        return mapper.mapToItemDto(repository.save(oldItem));
    }
}
