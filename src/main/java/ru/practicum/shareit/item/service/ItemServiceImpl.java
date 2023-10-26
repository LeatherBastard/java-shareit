package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final ItemMapper mapper;

    @Override
    public List<ItemDto> getAll() {
        return repository.getAll().stream().map(mapper::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllByOwner(int ownerId) {
        return repository.getAllByOwner(ownerId).stream().map(mapper::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllByText(String text) {
        return repository.getAllByText(text).stream().map(mapper::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Integer id) {
        return mapper.mapToItemDto(repository.getById(id));
    }

    @Override
    public ItemDto add(int ownerId, ItemDto item) {
        return mapper.mapToItemDto(repository.add(ownerId, mapper.mapToItem(item)));
    }

    @Override
    public ItemDto update(int ownerId, ItemDto item) {

        return mapper.mapToItemDto(repository.update(ownerId, mapper.mapToItem(item)));
    }
}
