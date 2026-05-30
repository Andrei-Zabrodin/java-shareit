package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String OWNER_HEADER = "X-Sharer-User-Id";

    private final ItemMapper itemMapper;
    private final ItemService itemService;

    @GetMapping
    public List<ResponseItemDto> getItems(@RequestHeader(OWNER_HEADER) long ownerId) {
        return itemService.getItems(ownerId).stream()
                .map(itemMapper::convertToDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseItemDto getItemById(@PathVariable long id) {
        return itemMapper.convertToDto(itemService.getItemById(id));
    }

    @GetMapping("/search?text={text}")
    public List<ResponseItemDto> getItemsBySearch(@RequestParam String text) {
        return itemService.getItemsBySearch(text).stream()
                .map(itemMapper::convertToDto)
                .toList();
    }

    @PostMapping
    public ResponseItemDto postItem (@Valid @RequestBody RequestItemDto itemDto,
                             @RequestHeader(OWNER_HEADER) long ownerId) {
        Item postedItem = itemService.postItem(itemMapper.convertToEntity(itemDto), ownerId);
        return itemMapper.convertToDto(postedItem);
    }

    @PatchMapping("/{id}")
    public ResponseItemDto patchItem (@RequestBody RequestItemDto itemDto,
                                      @RequestHeader(OWNER_HEADER) long ownerId,
                                      @PathVariable long id) {
        Item patchedItem = itemService.patchItem(itemMapper.convertToEntity(itemDto), ownerId, id);
        return itemMapper.convertToDto(patchedItem);
    }


}
