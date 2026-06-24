package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getUsers() {
        log.info("GET /users – Запрос всех пользователей");

        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        log.info("GET /users/{} - Запрос пользователя по id", id);

        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto postUser(@RequestBody UserDto userDto) {
        log.info("POST /users - Создание нового пользователя");

        return userService.save(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@RequestBody UserDto userDto,
                                     @PathVariable long id) {
        log.info("PATCH /users/{} - Обновление пользователя", id);

        return userService.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("DELETE /users/{} - Удаление пользователя", id);

        userService.delete(id);
    }

}
