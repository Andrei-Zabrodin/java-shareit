package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_HEADER) long ownerId) {
        log.info("GET /items – Запрос всех вещей пользователя с id: {}", ownerId);
        return itemClient.getItems(ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_HEADER) long userId,
                                              @PathVariable long itemId) {
        log.info("GET /items/{} - Запрос вещи по id", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearch(@RequestHeader(USER_HEADER) long userId,
                                                   @RequestParam String text) {
        log.info("GET /items/search - Поиск доступных для аренды вещей по строке: '{}'", text);
        return itemClient.getItemsBySearch(userId, text);
    }

    @PostMapping
    public ResponseEntity<Object> postItem(@Valid @RequestBody ItemDto itemDto,
                            @RequestHeader(USER_HEADER) long ownerId) {
        log.info("POST /items - Создание новой вещи: {}, id владельца: {}", itemDto.getName(), ownerId);
        return itemClient.postItem(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchItem(@RequestBody ItemDto itemDto,
                             @RequestHeader(USER_HEADER) long ownerId,
                             @PathVariable long id) {
        log.info("PATCH /items/{} - Обновление вещи пользователем {}", id, ownerId);
        return itemClient.patchItem(itemDto, ownerId, id);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestBody @Valid CommentDto comment,
                                              @RequestHeader(USER_HEADER) long userId,
                                              @PathVariable long itemId) {
        log.info("POST /items/{}/comment - Комментирование вещи пользователем {}", itemId, userId);
        return itemClient.postComment(comment, userId, itemId);
    }
}
