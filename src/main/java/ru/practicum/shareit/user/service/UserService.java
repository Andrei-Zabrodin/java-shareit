package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getUsers();

    UserDto getUserById(long id);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto, long id);

    void delete(long id);

    void existsByEmail(User user);
}
