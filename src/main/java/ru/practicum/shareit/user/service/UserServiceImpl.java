package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        return userRepository.getUserById(id);
    }

    @Override
    public User save(User user) {
        userRepository.existsByEmail(user);

        return userRepository.save(user);
    }

    @Override
    public User update(User user, long id) {
        userRepository.getUserById(id);
        userRepository.existsByEmail(user);

        user.setId(id);
        return userRepository.update(user);
    }

    @Override
    public void delete(long id) {
        userRepository.getUserById(id);
        userRepository.delete(id);
    }
}
