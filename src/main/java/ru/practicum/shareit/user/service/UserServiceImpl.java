package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateObjectException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Пользователя с id " + id + " нет в базе!"));
    }

    @Override
    public User save(User user) {
        existsByEmail(user);

        return userRepository.save(user);
    }

    @Override
    public User update(User newUser, long id) {
        log.debug("На обновление переданы следующие данные: {}", newUser.toString());

        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Пользователя с id " + id + " нет в базе!"));
        existsByEmail(newUser);

        Optional.ofNullable(newUser.getName()).ifPresent(oldUser::setName);
        Optional.ofNullable(newUser.getEmail()).ifPresent(oldUser::setEmail);

        return userRepository.save(oldUser);
    }

    @Override
    public void delete(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Пользователя с id " + id + " нет в базе!"));

        userRepository.delete(user);
    }

    @Override
    public void existsByEmail(User user) {
        log.debug("Проверка существования адреса почты: {}", user.getEmail());

        List<User> users = userRepository.findByEmail(user.getEmail());
        if (!users.isEmpty()) {
            log.debug("Адрес {} уже есть в базе", user.getEmail());
            throw new DuplicateObjectException("Адрес почты " + user.getEmail() + " уже используется!");
        }
    }
}
