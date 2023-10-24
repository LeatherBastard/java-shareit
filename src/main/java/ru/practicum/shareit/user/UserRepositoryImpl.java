package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    Set<User> repository = new HashSet<>();

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
        throw new UserNotFoundException();
    }

    @Override
    public User add(User user) {
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
    }
}
