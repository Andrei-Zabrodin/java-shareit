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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    private ItemDto itemDto;
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

        itemDto = new ItemDto();
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
    void approveShouldApproveBooking() {
        BookingResponseDto savedBooking = bookingService.save(booker.getId(), bookingRequestDto);

        BookingResponseDto approvedBooking = bookingService.approve(owner.getId(), savedBooking.getId(), true);

        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getBookingsForBookerByStateShouldReturnFilteredBookings() {
        bookingService.save(booker.getId(), bookingRequestDto);

        List<BookingResponseDto> bookingsFuture = bookingService.getBookingsForBookerByState(
                booker.getId(), BookingsRequestState.FUTURE);
        List<BookingResponseDto> bookingsPast = bookingService.getBookingsForBookerByState(
                booker.getId(), BookingsRequestState.PAST);

        assertThat(bookingsFuture).hasSize(1);
        assertThat(bookingsPast).hasSize(0);
    }

    @Test
    void getBookingsForOwnerByState_ShouldReturnFilteredBookings() {
        bookingService.save(owner.getId(), bookingRequestDto);

        List<BookingResponseDto> bookingsFuture = bookingService.getBookingsForBookerByState(
                owner.getId(), BookingsRequestState.FUTURE);
        List<BookingResponseDto> bookingsPast = bookingService.getBookingsForBookerByState(
                owner.getId(), BookingsRequestState.PAST);

        assertThat(bookingsFuture).hasSize(1);
        assertThat(bookingsPast).hasSize(0);
    }
}