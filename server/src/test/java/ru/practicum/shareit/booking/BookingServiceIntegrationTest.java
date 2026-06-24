package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.controller.BookingsRequestState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.OwnershipConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private ItemDto savedItem;
    private BookingRequestDto bookingRequestDto;

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

        ItemDto itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        savedItem = itemService.save(itemDto, owner.getId());

        Instant now = Instant.now();
        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(now.plusSeconds(3600));
        bookingRequestDto.setEnd(now.plusSeconds(7200));
        bookingRequestDto.setItemId(savedItem.getId());
    }

    @Test
    void saveShouldCreateBooking() {
        BookingResponseDto savedBooking = bookingService.save(booker.getId(), bookingRequestDto);

        Booking booking = bookingRepository.findById(savedBooking.getId()).orElseThrow();

        assertThat(booking.getId()).isEqualTo(savedBooking.getId());
        assertThat(booking.getStart()).isEqualTo(bookingRequestDto.getStart());
        assertThat(booking.getEnd()).isEqualTo(bookingRequestDto.getEnd());
        assertThat(booking.getItem().getId()).isEqualTo(savedItem.getId());
        assertThat(booking.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBookingShouldReturnBooking() {
        BookingResponseDto savedBooking = bookingService.save(booker.getId(), bookingRequestDto);

        BookingResponseDto foundBooking = bookingService.getBooking(booker.getId(), savedBooking.getId());

        assertThat(foundBooking.getId()).isEqualTo(savedBooking.getId());
        assertThat(foundBooking.getStart()).isEqualTo(bookingRequestDto.getStart());
        assertThat(foundBooking.getEnd()).isEqualTo(bookingRequestDto.getEnd());
        assertThat(foundBooking.getItem().getId()).isEqualTo(savedItem.getId());
        assertThat(foundBooking.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(foundBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBookingWithNotOwnerOrNotBookerShouldThrowException() {
        BookingResponseDto savedBooking = bookingService.save(booker.getId(), bookingRequestDto);

        UserDto newUser = new UserDto();
        newUser.setName("newUser");
        newUser.setEmail("newUser@mail.ru");
        UserDto savedUser = userService.save(newUser);

        assertThatThrownBy(() -> bookingService.getBooking(savedUser.getId(), savedBooking.getId()))
                .isInstanceOf(OwnershipConflictException.class)
                .hasMessageContaining("Получить данные о бронировании вещи может только её владелец или автор бронирования");
    }

    @Test
    void approveShouldApproveBooking() {
        BookingResponseDto savedBooking = bookingService.save(booker.getId(), bookingRequestDto);

        BookingResponseDto approvedBooking = bookingService.approve(owner.getId(), savedBooking.getId(), true);

        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveWithWrongOwnerShouldThrowException() {
        BookingResponseDto savedBooking = bookingService.save(booker.getId(), bookingRequestDto);

        assertThatThrownBy(() -> bookingService.approve(booker.getId(), savedBooking.getId(), true))
                .isInstanceOf(OwnershipConflictException.class)
                .hasMessageContaining("Вы не являетесь владельцем вещи с id");
    }

    @Test
    void getBookingsForBookerByStateShouldReturnBookings() {
        bookingService.save(booker.getId(), bookingRequestDto);

        BookingRequestDto bookingCurrent = new BookingRequestDto();
        bookingCurrent.setStart(Instant.now().minusSeconds(3600));
        bookingCurrent.setEnd(Instant.now().plusSeconds(7200));
        bookingCurrent.setItemId(savedItem.getId());
        bookingService.save(booker.getId(), bookingCurrent);

        BookingRequestDto bookingRejected1 = new BookingRequestDto();
        bookingRejected1.setStart(Instant.now().plusSeconds(5000));
        bookingRejected1.setEnd(Instant.now().plusSeconds(10000));
        bookingRejected1.setItemId(savedItem.getId());
        BookingResponseDto savedRejected1 = bookingService.save(booker.getId(), bookingRejected1);
        bookingService.approve(owner.getId(), savedRejected1.getId(), false);

        BookingRequestDto bookingRejected2 = new BookingRequestDto();
        bookingRejected2.setStart(Instant.now().plusSeconds(400));
        bookingRejected2.setEnd(Instant.now().plusSeconds(4500));
        bookingRejected2.setItemId(savedItem.getId());
        BookingResponseDto savedRejected2 = bookingService.save(booker.getId(), bookingRejected2);
        bookingService.approve(owner.getId(), savedRejected2.getId(), false);


        List<BookingResponseDto> bookingsFuture = bookingService.getBookingsForBookerByState(
                booker.getId(), BookingsRequestState.FUTURE);
        List<BookingResponseDto> bookingsPast = bookingService.getBookingsForBookerByState(
                booker.getId(), BookingsRequestState.PAST);
        List<BookingResponseDto> bookingsCurrent = bookingService.getBookingsForBookerByState(
                booker.getId(), BookingsRequestState.CURRENT);
        List<BookingResponseDto> bookingsWaiting = bookingService.getBookingsForBookerByState(
                booker.getId(), BookingsRequestState.WAITING);
        List<BookingResponseDto> bookingsRejected = bookingService.getBookingsForBookerByState(
                booker.getId(), BookingsRequestState.REJECTED);
        List<BookingResponseDto> bookingsDefault = bookingService.getBookingsForBookerByState(
                booker.getId(), BookingsRequestState.ALL);

        assertThat(bookingsFuture).hasSize(3);
        assertThat(bookingsPast).hasSize(0);
        assertThat(bookingsCurrent).hasSize(1);
        assertThat(bookingsWaiting).hasSize(2);
        assertThat(bookingsRejected).hasSize(2);
        assertThat(bookingsDefault).hasSize(4);
    }

    @Test
    void getBookingsForOwnerByStateShouldReturnBookings() {
        bookingService.save(booker.getId(), bookingRequestDto);

        BookingRequestDto bookingCurrent = new BookingRequestDto();
        bookingCurrent.setStart(Instant.now().minusSeconds(3600));
        bookingCurrent.setEnd(Instant.now().plusSeconds(7200));
        bookingCurrent.setItemId(savedItem.getId());
        bookingService.save(booker.getId(), bookingCurrent);

        BookingRequestDto bookingRejected1 = new BookingRequestDto();
        bookingRejected1.setStart(Instant.now().plusSeconds(5000));
        bookingRejected1.setEnd(Instant.now().plusSeconds(10000));
        bookingRejected1.setItemId(savedItem.getId());
        BookingResponseDto savedRejected1 = bookingService.save(booker.getId(), bookingRejected1);
        bookingService.approve(owner.getId(), savedRejected1.getId(), false);

        BookingRequestDto bookingRejected2 = new BookingRequestDto();
        bookingRejected2.setStart(Instant.now().plusSeconds(400));
        bookingRejected2.setEnd(Instant.now().plusSeconds(4500));
        bookingRejected2.setItemId(savedItem.getId());
        BookingResponseDto savedRejected2 = bookingService.save(booker.getId(), bookingRejected2);
        bookingService.approve(owner.getId(), savedRejected2.getId(), false);

        List<BookingResponseDto> bookingsFuture = bookingService.getBookingsForOwnerByState(
                owner.getId(), BookingsRequestState.FUTURE);
        List<BookingResponseDto> bookingsPast = bookingService.getBookingsForOwnerByState(
                owner.getId(), BookingsRequestState.PAST);
        List<BookingResponseDto> bookingsCurrent = bookingService.getBookingsForOwnerByState(
                owner.getId(), BookingsRequestState.CURRENT);
        List<BookingResponseDto> bookingsWaiting = bookingService.getBookingsForOwnerByState(
                owner.getId(), BookingsRequestState.WAITING);
        List<BookingResponseDto> bookingsRejected = bookingService.getBookingsForOwnerByState(
                owner.getId(), BookingsRequestState.REJECTED);
        List<BookingResponseDto> bookingsDefault = bookingService.getBookingsForOwnerByState(
                owner.getId(), BookingsRequestState.ALL);

        assertThat(bookingsFuture).hasSize(3);
        assertThat(bookingsPast).hasSize(0);
        assertThat(bookingsCurrent).hasSize(1);
        assertThat(bookingsWaiting).hasSize(2);
        assertThat(bookingsRejected).hasSize(2);
        assertThat(bookingsDefault).hasSize(4);
    }

    @Test
    void validateBookingWhenEndBeforeStartShouldThrowException() {
        bookingRequestDto.setStart(Instant.now());
        bookingRequestDto.setEnd(Instant.now().minusSeconds(100));

        assertThatThrownBy(() -> bookingService.save(booker.getId(), bookingRequestDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Дата конца бронирования должна быть после даты начала!");
    }

    @Test
    void validateBookingWithItemNotAvailableShouldThrowException() {
        ItemDto newItem = new ItemDto();
        newItem.setName("newItem");
        newItem.setDescription("newDescription");
        newItem.setAvailable(false);
        ItemDto savedNewItem = itemService.save(newItem, owner.getId());
        bookingRequestDto.setItemId(savedNewItem.getId());

        assertThatThrownBy(() -> bookingService.save(booker.getId(), bookingRequestDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Вещь с id " + savedNewItem.getId() + " недоступна к бронированию!");
    }
}