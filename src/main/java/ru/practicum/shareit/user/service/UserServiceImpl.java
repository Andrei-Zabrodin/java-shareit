package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateObjectException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
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
    private final UserMapper userMapper;

    @Override
    public Collection<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Пользователя с id " + id + " нет в базе!"));

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto save(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        existsByEmail(user);

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(UserDto userDto, long id) {
        User newUser = userMapper.toEntity(userDto);
        log.debug("На обновление переданы следующие данные: {}", newUser.toString());

        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Пользователя с id " + id + " нет в базе!"));
        existsByEmail(newUser);

        Optional.ofNullable(newUser.getName()).ifPresent(oldUser::setName);
        Optional.ofNullable(newUser.getEmail()).ifPresent(oldUser::setEmail);

        return userMapper.toUserDto(userRepository.save(oldUser));
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
