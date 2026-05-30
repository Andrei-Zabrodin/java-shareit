package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getUsers();

    User getUserById(long id);

    User postUser(User user);

    User patchUser(User user, long id);

    void deleteUser(long id);
}
