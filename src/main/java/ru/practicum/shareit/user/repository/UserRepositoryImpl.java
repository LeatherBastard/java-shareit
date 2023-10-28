package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final String USER_NOT_FOUND_MESSAGE = "User with id %d not found";
    private static final String DUPLICATE_EMAIL_MESSAGE = "User with this email already exists!";
    private final List<User> repository = new ArrayList<>();
    private static int id = 1;

    @Override
    public List<User> getAll() {
        return repository;
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
        if (isDuplicateEmail(user.getEmail()))
            throw new DuplicateEmailException(DUPLICATE_EMAIL_MESSAGE);
        user.setId(id);
        incrementId();
        repository.add(user);
        return getById(user.getId());
    }

    @Override
    public User update(int id, User user) {

        User oldUser = getById(id);
        if (!oldUser.getEmail().equals(user.getEmail()) && isDuplicateEmail(user.getEmail()))
            throw new DuplicateEmailException(DUPLICATE_EMAIL_MESSAGE);
        if (user.getName() != null)
            oldUser.setName(user.getName());
        if (user.getEmail() != null)
            oldUser.setEmail(user.getEmail());
        return getById(id);
    }

    @Override
    public void remove(int id) {
        User user = getById(id);
        repository.remove(user);

    }

    private boolean isDuplicateEmail(String email) {
        for (User user : repository) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private static void incrementId() {
        id++;
    }


}
