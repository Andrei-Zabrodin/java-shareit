package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.model.ItemShort;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public Collection<ItemRequestDto> getItemRequests(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id " + userId + " не найден!"));

        Map<Long, ItemRequest> requestMap = itemRequestRepository.findAllByAuthorIdOrderByCreatedDesc(userId).stream()
                .collect(Collectors.toMap(ItemRequest::getId, request -> request));

        Map<Long, List<ItemShort>> itemMap = itemRepository.findAllByItemRequestIdIn(requestMap.keySet()).stream()
                .collect(Collectors.groupingBy(ItemShort::getItemRequestId));

        return requestMap.values().stream()
                .map(request -> itemRequestMapper.convertToDtoWithItems(
                        request,
                        itemMap.getOrDefault(request.getId(), null)
                ))
                .toList();
    }

    @Override
    public Collection<ItemRequestDto> getOtherItemRequests(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id " + userId + " не найден!"));

        return itemRequestRepository.findAllByAuthorIdNotOrderByCreatedDesc(userId).stream()
                .map(itemRequestMapper::convertToDto)
                .toList();
    }

    @Override
    public ItemRequestDto getItemRequest(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id " + userId + " не найден!"));
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new IdNotFoundException("Запрос с id " + requestId + " не найден!"));

        Collection<ItemShort> items = itemRepository.findAllByItemRequestId(requestId);
        return itemRequestMapper.convertToDtoWithItems(request, items);
    }

    @Override
    public ItemRequestDto save(long userId, ItemRequestDto dto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id " + userId + " не найден!"));

        ItemRequest request = itemRequestMapper.convertToEntity(dto);
        request.setCreated(Instant.now());
        request.setAuthor(author);

        return itemRequestMapper.convertToDto(itemRequestRepository.save(request));
    }
}
