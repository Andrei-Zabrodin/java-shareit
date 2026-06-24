package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("GET /users – Запрос всех пользователей");
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        log.info("GET /users/{} - Запрос пользователя по id", id);
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> postUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST /users - Создание нового пользователя");
        return userClient.postUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(@RequestBody UserDto userDto,
                             @PathVariable long id) {
        log.info("PATCH /users/{} - Обновление пользователя", id);
        return userClient.patchUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("DELETE /users/{} - Удаление пользователя", id);
        userClient.deleteUser(id);
    }
}
