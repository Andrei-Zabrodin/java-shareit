package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> getUsers();
    Optional<User> getUserById(long id);
    User postUser(User user);
    User patchUser(User user, long id);
    void deleteUser(long id);
    void existsByEmail(User user);
    void existsById(long id);
}
