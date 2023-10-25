package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final String USER_NOT_FOUND_MESSAGE = "User with id %d not found";
    Set<User> repository = new HashSet<>();
    private static int id = 1;

    @Override
    public List<User> getAll() {
        return repository.stream().collect(Collectors.toList());
    }

    @Override
    public User getById(int id) {
        for (User user : repository) {
            if (user.getId() == id) {
                return user;
            }
        }
        throw new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, id);
    }

    @Override
    public User add(User user) {
        user.setId(id);
        id++;
        repository.add(user);
        return getById(user.getId());
    }

    @Override
    public User update(User user) {
        User oldUser = getById(user.getId());
        repository.remove(oldUser);
        repository.add(user);
        return getById(user.getId());
    }

    @Override
    public void removeAll() {
        repository.clear();
        id = 0;
    }
}
