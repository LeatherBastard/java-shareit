package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto getById(int id);

    UserDto add(UserDto user);

    UserDto update(int id, UserDto user);

    void remove(int id);

}
