package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
    Collection<User> getUsers();

    User getUserById(long id);

    User save(User user);

    User update(User user);

    void delete(long id);

    void existsByEmail(User user);
}
