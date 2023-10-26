package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private static final String ITEM_NOT_FOUND_MESSAGE = "Item with id %d not found";
    private static final String WRONG_OWNER_MESSAGE = "You are not an owner ot this item!";
    Set<Item> repository = new HashSet<>();
    private static int id = 1;

    @Override
    public List<Item> getAll() {
        return repository.stream().collect(Collectors.toList());
    }

    @Override
    public List<Item> getAllByOwner(int ownerId) {
        return repository.stream().filter(item -> item.getOwnerId() == ownerId).collect(Collectors.toList());
    }

    @Override
    public List<Item> getAllByText(String text) {
        return repository.stream()
                .filter(item -> (item.getName().contains(text) || item.getDescription().contains(text)) && item.isAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(Integer id) {
        for (Item item : repository) {
            if (item.getId() == id) {
                return item;
            }
        }
        throw new EntityNotFoundException(ITEM_NOT_FOUND_MESSAGE, id);
    }

    @Override
    public Item add(int ownerId, Item item) {
        item.setId(id);
        id++;
        item.setOwnerId(ownerId);
        repository.add(item);
        return getById(item.getId());
    }

    @Override
    public Item update(int ownerId, Item item) {

        Item oldItem = getById(item.getId());
        if (oldItem.getOwnerId() != ownerId) {
            throw new WrongOwnerException(WRONG_OWNER_MESSAGE);
        }
        oldItem.setName(item.getName());
        oldItem.setDescription(item.getDescription());
        oldItem.setAvailable(item.isAvailable());
        return getById(item.getId());
    }

    @Override
    public void removeAll() {
        repository.clear();
        id = 0;
    }
}
