package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getUsers();

    User getUserById(long id);

    User save(User user);

    User update(User user, long id);

    void delete(long id);
}
