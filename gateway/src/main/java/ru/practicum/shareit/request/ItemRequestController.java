package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader(USER_HEADER) long userId) {
        log.info("GET /requests - Получения списка запросов вещей пользователя с id {}", userId);
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsOfOtherUsers(@RequestHeader(USER_HEADER) long userId) {
        log.info("GET /requests/all - Получения списка запросов вещей других пользователей," +
                "id текущего пользователя - {}", userId);
        return itemRequestClient.getRequestsOfOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestsById(@RequestHeader(USER_HEADER) long userId,
                                                      @PathVariable long requestId) {
        log.info("GET /requests/{} - Получения запроса на вещь от пользователя с id {},", requestId, userId);
        return itemRequestClient.getRequestById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> postItemRequest(@RequestHeader(USER_HEADER) long userId,
                                                  @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST /requests - Создание нового запросе на вещь, id создателя: {}", userId);
        return itemRequestClient.postItemRequest(userId, itemRequestDto);
    }
}
