package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @GetMapping
    public Collection<ItemRequestDto> getItemRequests(@RequestHeader(USER_HEADER) long userId) {
        log.info("GET /requests - Получения списка запросов вещей пользователя с id {}", userId);

        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/all")
    public  Collection<ItemRequestDto> getItemRequestsOfOtherUsers(@RequestHeader(USER_HEADER) long userId) {
        log.info("GET /requests/all - Получения списка запросов вещей других пользователей," +
                "id текущего пользователя - {}", userId);

        return itemRequestService.getOtherItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestsById(@RequestHeader(USER_HEADER) long userId,
                                                      @PathVariable long requestId) {
        log.info("GET /requests/{} - Получения запроса на вещь от пользователя с id {},", requestId, userId);

        return itemRequestService.getItemRequest(userId, requestId);
    }

    @PostMapping
    public ItemRequestDto postItemRequest(@RequestHeader(USER_HEADER) long userId,
                                                  @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST /requests - Создание нового запросе на вещь, id создателя: {}", userId);

        return itemRequestService.save(userId, itemRequestDto);
    }
}
