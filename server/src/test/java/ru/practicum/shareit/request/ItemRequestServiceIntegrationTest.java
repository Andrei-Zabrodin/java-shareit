package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User author;
    private User otherUser;
    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        UserDto authorDto = new UserDto();
        authorDto.setName("author");
        authorDto.setEmail("author@mail.ru");
        UserDto savedAuthor = userService.save(authorDto);
        author = userRepository.findById(savedAuthor.getId()).orElseThrow();

        UserDto otherUserDto = new UserDto();
        otherUserDto.setName("otherUser");
        otherUserDto.setEmail("other@mail.ru");
        UserDto savedOtherUser = userService.save(otherUserDto);
        otherUser = userRepository.findById(savedOtherUser.getId()).orElseThrow();

        requestDto = new ItemRequestDto();
        requestDto.setDescription("description");
    }

    @Test
    void saveShouldCreateRequest() {
        ItemRequestDto savedRequest = itemRequestService.save(author.getId(), requestDto);

        ItemRequest request = itemRequestRepository.findById(savedRequest.getId()).orElseThrow();

        assertThat(request.getId()).isEqualTo(savedRequest.getId());
        assertThat(request.getDescription()).isEqualTo(requestDto.getDescription());
        assertThat(request.getAuthor().getId()).isEqualTo(author.getId());
    }

    @Test
    void getItemRequestsShouldReturnRequestsForUser() {
        itemRequestService.save(author.getId(), requestDto);

        ItemRequestDto anotherRequest = new ItemRequestDto();
        anotherRequest.setDescription("otherDescription");
        itemRequestService.save(otherUser.getId(), anotherRequest);

        Collection<ItemRequestDto> requests = itemRequestService.getItemRequests(author.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests).extracting(ItemRequestDto::getDescription)
                .containsExactly("description");
    }

    @Test
    void getOtherItemRequestsShouldReturnRequestsFromOtherUsers() {
        itemRequestService.save(author.getId(), requestDto);

        ItemRequestDto anotherRequest = new ItemRequestDto();
        anotherRequest.setDescription("otherDescription");
        itemRequestService.save(otherUser.getId(), anotherRequest);

        Collection<ItemRequestDto> requests = itemRequestService.getOtherItemRequests(author.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests).extracting(ItemRequestDto::getDescription)
                .containsExactly("otherDescription");
    }

    @Test
    void getItemRequestShouldReturnRequestById() {
        ItemRequestDto savedRequest = itemRequestService.save(author.getId(), requestDto);

        ItemRequestDto foundRequest = itemRequestService.getItemRequest(author.getId(), savedRequest.getId());

        assertThat(foundRequest.getId()).isEqualTo(savedRequest.getId());
        assertThat(foundRequest.getDescription()).isEqualTo(requestDto.getDescription());
    }
}