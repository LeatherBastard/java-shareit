package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAll();

    List<Item> getAllByOwner(int ownerId);

    List<Item> getAllByText(String text);

    Item getById(Integer id);

    Item add(int ownerId, Item item);

    Item update(int ownerId, int itemId, Item item);

    void removeAll();
}
