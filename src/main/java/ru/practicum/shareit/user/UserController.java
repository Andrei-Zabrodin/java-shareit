package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
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
    public List<ResponseUserDto> getUsers() {
        log.info("GET /users – Запрос всех пользователей");

        return userService.getUsers().stream()
                .map(userMapper::toResponseUserDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseUserDto getUserById(@PathVariable long id) {
        log.info("GET /users/{} - Запрос пользователя по id", id);

        return userMapper.toResponseUserDto(userService.getUserById(id));
    }

    @PostMapping
    public ResponseUserDto postUser(@Valid @RequestBody RequestUserDto userDto) {
        log.info("POST /users - Создание нового пользователя");

        User user = userService.postUser(userMapper.toEntity(userDto));
        return userMapper.toResponseUserDto(user);
    }

    @PatchMapping("/{id}")
    public ResponseUserDto patchUser(@RequestBody RequestUserDto userDto,
                                     @PathVariable long id) {
        log.info("PATCH /users/{} - Обновление пользователя", id);

        User user = userService.patchUser(userMapper.toEntity(userDto), id);
        return userMapper.toResponseUserDto(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("DELETE /users/{} - Удаление пользователя", id);

        userService.deleteUser(id);
    }

}
