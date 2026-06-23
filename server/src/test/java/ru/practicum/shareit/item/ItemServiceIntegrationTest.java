package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.OwnershipConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        UserDto ownerDto = new UserDto();
        ownerDto.setName("owner");
        ownerDto.setEmail("owner@mail.ru");
        UserDto savedOwner = userService.save(ownerDto);
        owner = userRepository.findById(savedOwner.getId()).orElseThrow();

        UserDto bookerDto = new UserDto();
        bookerDto.setName("booker");
        bookerDto.setEmail("booker@mail.ru");
        UserDto savedBooker = userService.save(bookerDto);
        booker = userRepository.findById(savedBooker.getId()).orElseThrow();

        itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
    }

    @Test
    void saveShouldCreateItem() {
        ItemDto savedItem = itemService.save(itemDto, owner.getId());

        Item item = itemRepository.findById(savedItem.getId()).orElseThrow();

        assertThat(item.getId()).isEqualTo(savedItem.getId());
        assertThat(item.getName()).isEqualTo(itemDto.getName());
        assertThat(item.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(item.getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(item.getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void saveWithRequestIdShouldCreateItem() {
        ItemRequestDto request = new ItemRequestDto();
        request.setDescription("request");
        ItemRequestDto savedRequest = itemRequestService.save(booker.getId(), request);

        itemDto.setRequestId(savedRequest.getId());

        ItemDto savedItem = itemService.save(itemDto, owner.getId());

        Item item = itemRepository.findById(savedItem.getId()).orElseThrow();

        assertThat(item.getId()).isEqualTo(savedItem.getId());
        assertThat(item.getName()).isEqualTo(itemDto.getName());
        assertThat(item.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(item.getAvailable()).isEqualTo(itemDto.getAvailable());
        assertThat(item.getOwner().getId()).isEqualTo(owner.getId());
        assertThat(item.getItemRequest().getId()).isEqualTo(savedRequest.getId());
    }

    @Test
    void saveWithWrongRequestIdShouldThrowException() {
        ItemRequestDto request = new ItemRequestDto();
        request.setDescription("request");
        itemRequestService.save(booker.getId(), request);

        long wrongRequestId = 2L;

        itemDto.setRequestId(wrongRequestId);

        assertThatThrownBy(() -> itemService.save(itemDto, owner.getId()))
                .isInstanceOf(IdNotFoundException.class)
                .hasMessageContaining("Запроса с id " + wrongRequestId + " нет в базе!");
    }

    @Test
    void getItemsShouldReturnAllItemsForOwner() {
        itemService.save(itemDto, owner.getId());

        ItemDto anotherItem = new ItemDto();
        anotherItem.setName("newItem");
        anotherItem.setDescription("newDescription");
        anotherItem.setAvailable(true);
        itemService.save(anotherItem, booker.getId());

        Collection<ItemDto> items = itemService.getItems(owner.getId());

        assertThat(items).hasSize(1);
        assertThat(items).extracting(ItemDto::getName)
                .containsExactly(itemDto.getName());
    }

    @Test
    void getItemByIdShouldReturnItem() {
        ItemDto savedItem = itemService.save(itemDto, owner.getId());

        ItemDto foundItem = itemService.getItemById(savedItem.getId());

        assertThat(foundItem.getId()).isEqualTo(savedItem.getId());
        assertThat(foundItem.getName()).isEqualTo(itemDto.getName());
        assertThat(foundItem.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(foundItem.getAvailable()).isEqualTo(itemDto.getAvailable());
    }

    @Test
    void getItemsBySearchShouldReturnMatchingItems() {
        itemService.save(itemDto, owner.getId());

        ItemDto anotherItem = new ItemDto();
        anotherItem.setName("thing");
        anotherItem.setDescription("report");
        anotherItem.setAvailable(true);
        itemService.save(anotherItem, owner.getId());

        Collection<ItemDto> items = itemService.getItemsBySearch("tem");

        assertThat(items).hasSize(1);
        assertThat(items).extracting(ItemDto::getName)
                .containsExactly(itemDto.getName());
    }

    @Test
    void getItemsBySearch_WithEmptyText_ShouldReturnEmptyList() {
        Collection<ItemDto> results = itemService.getItemsBySearch("");

        assertThat(results).isEmpty();
    }

    @Test
    void updateShouldUpdateItem() {
        ItemDto savedItem = itemService.save(itemDto, owner.getId());

        ItemDto updateDto = new ItemDto();
        updateDto.setName("newName");
        updateDto.setDescription("newDescription");
        updateDto.setAvailable(false);

        ItemDto updatedItem = itemService.update(updateDto, owner.getId(), savedItem.getId());

        assertThat(updatedItem.getId()).isEqualTo(savedItem.getId());
        assertThat(updatedItem.getName()).isEqualTo(updateDto.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(updateDto.getDescription());
        assertThat(updatedItem.getAvailable()).isEqualTo(updateDto.getAvailable());
    }

    @Test
    void updateWithNotOwnerShouldThrowException() {
        ItemDto savedItem = itemService.save(itemDto, owner.getId());

        assertThatThrownBy(() -> itemService.update(itemDto, booker.getId(), savedItem.getId()))
                .isInstanceOf(OwnershipConflictException.class)
                .hasMessageContaining("Вы не являетесь владельцем вещи с id" + savedItem.getId());
    }

    @Test
    void saveCommentShouldCreateComment() {
        ItemDto savedItem = itemService.save(itemDto, owner.getId());

        Booking booking = new Booking();
        booking.setStart(Instant.now().minusSeconds(3600));
        booking.setEnd(Instant.now().minusSeconds(1800));
        booking.setItem(itemRepository.findById(savedItem.getId()).orElseThrow());
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");

        CommentDto savedComment = itemService.save(commentDto, booker.getId(), savedItem.getId());

        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getText()).isEqualTo(commentDto.getText());
        assertThat(savedComment.getAuthorName()).isEqualTo(booker.getName());
        assertThat(savedComment.getCreated()).isNotNull();

        Comment comment = commentRepository.findById(savedComment.getId()).orElseThrow();
        assertThat(comment.getText()).isEqualTo(commentDto.getText());
        assertThat(comment.getItem().getId()).isEqualTo(savedItem.getId());
        assertThat(comment.getAuthor().getId()).isEqualTo(booker.getId());
    }

    @Test
    void saveCommentWithoutBookingShouldThrowException() {
        ItemDto savedItem = itemService.save(itemDto, owner.getId());

        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");

        assertThatThrownBy(() -> itemService.save(commentDto, booker.getId(), savedItem.getId()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Оставлять комментарии могут только пользователи, которые уже пользовались вещью!");
    }
}