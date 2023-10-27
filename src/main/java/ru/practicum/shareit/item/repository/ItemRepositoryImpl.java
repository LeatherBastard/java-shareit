package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.WrongOwnerException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private static final String ITEM_NOT_FOUND_MESSAGE = "Item with id %d not found";
    private static final String WRONG_OWNER_MESSAGE = "You are not an owner ot this item!";
    List<Item> repository = new ArrayList<>();
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
        List<Item> items;
        if (text.isEmpty()) {
            items = new ArrayList<>();
        } else {
            items = repository.stream()
                    .filter(
                            item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                                    || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                                    && item.getAvailable()
                    )
                    .collect(Collectors.toList());
        }
        return items;
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
    public Item update(int ownerId, int itemId, Item item) {

        Item oldItem = getById(itemId);
        if (oldItem.getOwnerId() != ownerId) {
            throw new WrongOwnerException(WRONG_OWNER_MESSAGE);
        }
        if (item.getName() != null)
            oldItem.setName(item.getName());
        if (item.getDescription() != null)
            oldItem.setDescription(item.getDescription());
        if (item.getAvailable() != null)
            oldItem.setAvailable(item.getAvailable());
        return getById(itemId);
    }

    @Override
    public void removeAll() {
        repository.clear();
        id = 0;
    }
}
