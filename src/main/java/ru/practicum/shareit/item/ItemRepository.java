package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository {
    List<Item> getAll();

    Item getById(Integer id);

    Item add(Item item);

    Item update(Item item);

    void removeAll();
}
