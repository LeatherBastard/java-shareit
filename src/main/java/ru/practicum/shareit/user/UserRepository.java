package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getAll();

    User getById(Integer id);

    User add(User user);

    User update(User user);

    void removeAll();

}
