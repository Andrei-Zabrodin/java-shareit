package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<User> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public User getUserById(long id) {
        return userRepository.getUserById(id)
                .orElseThrow(() -> new IdNotFoundException("Пользователя с id " + id + " нет в базе!"));
    }

    @Override
    public User postUser(User user) {
        userRepository.existsByEmail(user);

        return userRepository.postUser(user);
    }

    @Override
    public User patchUser(User user, long id) {
        userRepository.existsById(id);
        userRepository.existsByEmail(user);

        return userRepository.patchUser(user, id);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.existsById(id);
        userRepository.deleteUser(id);
    }
}
