package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateObjectException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class UserRepositoryMem implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 0;

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User postUser(User user) {
        user.setId(++currentId);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User patchUser(User user, long id) {
        User oldUser = users.get(id);

        Optional.ofNullable(user.getName()).ifPresent(oldUser::setName);
        Optional.ofNullable(user.getEmail()).ifPresent(oldUser::setEmail);

        return oldUser;
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    @Override
    public void existsByEmail(User user) {
        List<String> emails = users.values().stream()
                .map(User::getEmail)
                .toList();

        if (emails.contains(user.getEmail())) {
            throw new DuplicateObjectException("Адрес почты " + user.getEmail() + " уже используется!");
        }
    }

    @Override
    public void existsById(long id) {
         if (!users.containsKey(id)) {
             throw new IdNotFoundException("Пользователя с id " + id + " нет в базе!");
         }
    }
}
