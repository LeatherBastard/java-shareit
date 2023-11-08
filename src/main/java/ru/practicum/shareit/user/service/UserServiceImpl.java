package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public List<UserDto> getAll() {
        return repository.getAll().stream().map(mapper::mapToUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(int id) {
        return mapper.mapToUserDto(repository.getById(id));
    }

    @Override
    public UserDto add(UserDto user) {
        return mapper.mapToUserDto(repository.add(mapper.mapToUser(user)));
    }

    @Override
    public UserDto update(int id, UserDto user) {
        return mapper.mapToUserDto(repository.update(id, mapper.mapToUser(user)));
    }

    @Override
    public void remove(int id) {
        repository.remove(id);
    }
}
