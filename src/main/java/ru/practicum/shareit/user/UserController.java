package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("GET /users – Запрос всех пользователей");

        return userService.getUsers().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        log.info("GET /users/{} - Запрос пользователя по id", id);

        return userMapper.toUserDto(userService.getUserById(id));
    }

    @PostMapping
    public UserDto postUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST /users - Создание нового пользователя");

        User user = userService.save(userMapper.toEntity(userDto));
        return userMapper.toUserDto(user);
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@RequestBody UserDto userDto,
                                     @PathVariable long id) {
        log.info("PATCH /users/{} - Обновление пользователя", id);

        User user = userService.update(userMapper.toEntity(userDto), id);
        return userMapper.toUserDto(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("DELETE /users/{} - Удаление пользователя", id);

        userService.delete(id);
    }

}
