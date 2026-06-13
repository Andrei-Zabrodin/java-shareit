package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingWithDatesOnly;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDatesDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final ItemService itemService;
    private final BookingService bookingService;

    @GetMapping
    public List<ItemWithBookingDatesDto> getItems(@RequestHeader(USER_HEADER) long ownerId) {
        log.info("GET /items – Запрос всех вещей пользователя с id: {}", ownerId);

        Map<Long, Item> itemMap = itemService.getItems(ownerId).stream()
                .collect(Collectors.toMap(Item::getId, item -> item));

        Map<Long, BookingWithDatesOnly> prevBookings = bookingService.getPrevBookingsByItemIds(itemMap.keySet())
                .stream()
                .collect(Collectors.toMap(BookingWithDatesOnly::getItemId, booking -> booking));

        Map<Long, BookingWithDatesOnly> nextBookings = bookingService.getNextBookingsByItemIds(itemMap.keySet())
                .stream()
                .collect(Collectors.toMap(BookingWithDatesOnly::getItemId, booking -> booking));

        Map<Long, List<Comment>> commentsMap = itemService.getCommentsByItemIds(itemMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return itemMap.values().stream()
                .map(item -> itemMapper.convertToDto(
                        item,
                        prevBookings.getOrDefault(item.getId(), null),
                        nextBookings.getOrDefault(item.getId(), null),
                        commentsMap.getOrDefault(item.getId(), null)
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable long id) {
        log.info("GET /items/{} - Запрос вещи по id", id);

        Collection<Comment> comments = itemService.getCommentsByItemId(id);

        return itemMapper.convertToDto(itemService.getItemById(id), comments);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearch(@RequestParam String text) {
        log.info("GET /items/search - Поиск доступных для аренды вещей по строке: '{}'", text);

        return itemService.getItemsBySearch(text).stream()
                .map(itemMapper::convertToDto)
                .toList();
    }

    @PostMapping
    public ItemDto postItem(@Valid @RequestBody ItemDto itemDto,
                            @RequestHeader(USER_HEADER) long ownerId) {
        log.info("POST /items - Создание новой вещи: {}, id владельца: {}", itemDto.getName(), ownerId);

        Item postedItem = itemService.save(itemMapper.convertToEntity(itemDto), ownerId);
        return itemMapper.convertToDto(postedItem);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto,
                             @RequestHeader(USER_HEADER) long ownerId,
                             @PathVariable long id) {
        log.info("PATCH /items/{} - Обновление вещи пользователем {}", id, ownerId);

        Item patchedItem = itemService.update(itemMapper.convertToEntity(itemDto), ownerId, id);
        return itemMapper.convertToDto(patchedItem);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(USER_HEADER) long userId,
                                  @PathVariable long itemId,
                                  @RequestBody @Valid CommentDto comment) {
        log.info("POST /items/{}/comment - Комментирование вещи пользователем {}", itemId, userId);

        Comment postedComment = itemService.save(commentMapper.convertToEntity(comment), userId, itemId);

        return commentMapper.convertToDto(postedComment);
    }

}
