package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> getItems(@RequestHeader(USER_HEADER) long ownerId) {
        log.info("GET /items – Запрос всех вещей пользователя с id: {}", ownerId);

        return itemService.getItems(ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable long id) {
        log.info("GET /items/{} - Запрос вещи по id", id);

        return itemService.getItemById(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsBySearch(@RequestParam String text) {
        log.info("GET /items/search - Поиск доступных для аренды вещей по строке: '{}'", text);

        return itemService.getItemsBySearch(text);
    }

    @PostMapping
    public ItemDto postItem(@Valid @RequestBody ItemDto itemDto,
                            @RequestHeader(USER_HEADER) long ownerId) {
        log.info("POST /items - Создание новой вещи: {}, id владельца: {}", itemDto.getName(), ownerId);

        return itemService.save(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto,
                             @RequestHeader(USER_HEADER) long ownerId,
                             @PathVariable long id) {
        log.info("PATCH /items/{} - Обновление вещи пользователем {}", id, ownerId);

        return itemService.update(itemDto, ownerId, id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(USER_HEADER) long userId,
                                  @PathVariable long itemId,
                                  @RequestBody @Valid CommentDto comment) {
        log.info("POST /items/{}/comment - Комментирование вещи пользователем {}", itemId, userId);

        return itemService.save(comment, userId, itemId);
    }

}
