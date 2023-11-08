package ru.practicum.shareit.request;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository {
    List<ItemRequest> getAll();

    ItemRequest getById(Integer id);

    ItemRequest add(ItemRequest itemRequest);

    ItemRequest update(ItemRequest itemRequest);

    void removeAll();
}
