package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateObjectException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Slf4j
public class UserRepositoryMem implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 0;

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User getUserById(long id) {
        log.debug("Проверка существования пользователя с id: {}", id);
        if (!users.containsKey(id)) {
            log.debug("Пользователь с id: {} не найден", id);
            throw new IdNotFoundException("Пользователя с id " + id + " нет в базе!");
        }

        return users.get(id);
    }

    @Override
    public User save(User user) {
        user.setId(++currentId);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User newUser) {
        log.debug("На обновление переданы следующие данные: {}", newUser.toString());

        User oldUser = users.get(newUser.getId());
        Optional.ofNullable(newUser.getName()).ifPresent(oldUser::setName);
        Optional.ofNullable(newUser.getEmail()).ifPresent(oldUser::setEmail);

        return oldUser;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public void existsByEmail(User user) {
        log.debug("Проверка существования адреса почты: {}", user.getEmail());

        List<String> emails = users.values().stream()
                .map(User::getEmail)
                .toList();

        if (emails.contains(user.getEmail())) {
            log.debug("Адрес {} уже есть в базе", user.getEmail());
            throw new DuplicateObjectException("Адрес почты " + user.getEmail() + " уже используется!");
        }
    }
}
