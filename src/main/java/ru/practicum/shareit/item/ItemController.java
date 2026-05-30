package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String OWNER_HEADER = "X-Sharer-User-Id";

    private final ItemMapper itemMapper;
    private final ItemService itemService;

    @GetMapping
    public List<ResponseItemDto> getItems(@RequestHeader(OWNER_HEADER) long ownerId) {
        log.info("GET /items – Запрос всех вещей пользователя с id: {}", ownerId);

        return itemService.getItems(ownerId).stream()
                .map(itemMapper::convertToDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseItemDto getItemById(@PathVariable long id) {
        log.info("GET /items/{} - Запрос вещи по id", id);

        return itemMapper.convertToDto(itemService.getItemById(id));
    }

    @GetMapping("/search")
    public List<ResponseItemDto> getItemsBySearch(@RequestParam String text) {
        log.info("GET /items/search - Поиск доступных для аренды вещей по строке: '{}'", text);

        return itemService.getItemsBySearch(text).stream()
                .map(itemMapper::convertToDto)
                .toList();
    }

    @PostMapping
    public ResponseItemDto postItem(@Valid @RequestBody RequestItemDto itemDto,
                             @RequestHeader(OWNER_HEADER) long ownerId) {
        log.info("POST /items - Создание новой вещи: {}, id владельца: {}", itemDto.getName(), ownerId);

        Item postedItem = itemService.postItem(itemMapper.convertToEntity(itemDto), ownerId);
        return itemMapper.convertToDto(postedItem);
    }

    @PatchMapping("/{id}")
    public ResponseItemDto patchItem(@RequestBody RequestItemDto itemDto,
                                      @RequestHeader(OWNER_HEADER) long ownerId,
                                      @PathVariable long id) {
        log.info("PATCH /items/{} - Обновление вещи пользователем {}", id, ownerId);

        Item patchedItem = itemService.patchItem(itemMapper.convertToEntity(itemDto), ownerId, id);
        return itemMapper.convertToDto(patchedItem);
    }


}
