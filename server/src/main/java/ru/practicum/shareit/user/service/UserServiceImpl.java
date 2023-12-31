package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    public static final String USER_NOT_FOUND_MESSAGE = "User with id %d not found";

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream().map(mapper::mapToUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(int id) {
        return mapper.mapToUserDto(
                repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE, id))
        );
    }

    @Override
    public UserDto add(UserDto user) {
        return mapper.mapToUserDto(repository.save(mapper.mapToUser(user)));
    }

    @Override
    public UserDto update(int id, UserDto user) {
        User oldUser = mapper.mapToUser(getById(id));
        if (user.getName() != null)
            oldUser.setName(user.getName());
        if (user.getEmail() != null)
            oldUser.setEmail(user.getEmail());
        return mapper.mapToUserDto(repository.save(oldUser));
    }

    @Override
    public void remove(int id) {
        User user = mapper.mapToUser(getById(id));
        repository.delete(user);
    }
}
