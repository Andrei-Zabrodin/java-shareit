package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.controller.BookingsRequestState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IdNotFoundException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final long USER_ID = 1L;
    private static final long BOOKING_ID = 1L;
    private static final long WRONG_ID = 10L;
    private static final long ITEM_ID = 1L;

    private BookingResponseDto bookingResponseDtoApproved;
    private BookingResponseDto bookingResponseDtoRejected;
    private BookingResponseDto bookingResponseDtoPast;
    private BookingResponseDto bookingResponseDtoCurrent;
    private BookingResponseDto bookingResponseDtoWaiting;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();
        Instant start = now.plusSeconds(3600);
        Instant end = start.plusSeconds(3600);

        bookingResponseDtoApproved = new BookingResponseDto();
        bookingResponseDtoApproved.setId(BOOKING_ID);
        bookingResponseDtoApproved.setStart(start);
        bookingResponseDtoApproved.setEnd(end);
        bookingResponseDtoApproved.setStatus(BookingStatus.APPROVED);

        bookingResponseDtoRejected = new BookingResponseDto();
        bookingResponseDtoRejected.setId(2L);
        bookingResponseDtoRejected.setStart(start.plusSeconds(7200));
        bookingResponseDtoRejected.setEnd(end.plusSeconds(7200));
        bookingResponseDtoRejected.setStatus(BookingStatus.REJECTED);

        bookingResponseDtoPast = new BookingResponseDto();
        bookingResponseDtoPast.setId(3L);
        bookingResponseDtoPast.setStart(start.minusSeconds(7200));
        bookingResponseDtoPast.setEnd(end.minusSeconds(7200));
        bookingResponseDtoPast.setStatus(BookingStatus.APPROVED);

        bookingResponseDtoCurrent = new BookingResponseDto();
        bookingResponseDtoCurrent.setId(4L);
        bookingResponseDtoCurrent.setStart(start.minusSeconds(7200));
        bookingResponseDtoCurrent.setEnd(end.plusSeconds(7200));
        bookingResponseDtoCurrent.setStatus(BookingStatus.APPROVED);

        bookingResponseDtoWaiting = new BookingResponseDto();
        bookingResponseDtoWaiting.setId(5L);
        bookingResponseDtoWaiting.setStart(start.plusSeconds(10800));
        bookingResponseDtoWaiting.setEnd(end.plusSeconds(10800));
        bookingResponseDtoWaiting.setStatus(BookingStatus.WAITING);

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
        bookingRequestDto.setItemId(ITEM_ID);

    }

    // ==================== GET /bookings/{bookingId} ====================

    @Test
    void getBookingShouldReturnBooking() throws Exception {
        when(bookingService.getBooking(USER_ID, BOOKING_ID)).thenReturn(bookingResponseDtoApproved);

        mockMvc.perform(get("/bookings/{bookingId}", BOOKING_ID)
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDtoApproved.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDtoApproved.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingResponseDtoApproved.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingResponseDtoApproved.getStatus().toString())));

        verify(bookingService, times(1)).getBooking(USER_ID, BOOKING_ID);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingWithWrongBookingIdShouldReturnError() throws Exception {
        when(bookingService.getBooking(USER_ID, WRONG_ID)).thenThrow(IdNotFoundException.class);

        mockMvc.perform(get("/bookings/{bookingId}", WRONG_ID)
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).getBooking(USER_ID, WRONG_ID);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingWithWrongUserShouldReturnError() throws Exception {
        when(bookingService.getBooking(WRONG_ID, BOOKING_ID)).thenThrow(IdNotFoundException.class);

        mockMvc.perform(get("/bookings/{bookingId}", BOOKING_ID)
                        .header(USER_HEADER, WRONG_ID))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).getBooking(WRONG_ID, BOOKING_ID);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", BOOKING_ID))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBooking(anyLong(), anyLong());
    }


    // ==================== GET /bookings ====================

    @Test
    void getBookingsForBookerByStateWithStateAllShouldReturnAll() throws Exception {
        List<BookingResponseDto> bookings = Arrays.asList(bookingResponseDtoApproved, bookingResponseDtoRejected,
                bookingResponseDtoPast, bookingResponseDtoCurrent, bookingResponseDtoWaiting);
        when(bookingService.getBookingsForBookerByState(USER_ID, BookingsRequestState.ALL)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));

        verify(bookingService, times(1)).getBookingsForBookerByState(USER_ID, BookingsRequestState.ALL);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsForBookerByStateWithStatePastShouldReturnPast() throws Exception {
        List<BookingResponseDto> bookings = Arrays.asList(bookingResponseDtoPast);
        when(bookingService.getBookingsForBookerByState(USER_ID, BookingsRequestState.PAST)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "PAST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDtoPast.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingResponseDtoPast.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingResponseDtoPast.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDtoPast.getStatus().toString())));

        verify(bookingService, times(1)).getBookingsForBookerByState(USER_ID, BookingsRequestState.PAST);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsForBookerByStateWithStateCurrentShouldReturnCurrent() throws Exception {
        List<BookingResponseDto> bookings = Arrays.asList(bookingResponseDtoCurrent);
        when(bookingService.getBookingsForBookerByState(USER_ID, BookingsRequestState.CURRENT)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDtoCurrent.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingResponseDtoCurrent.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingResponseDtoCurrent.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDtoCurrent.getStatus().toString())));

        verify(bookingService, times(1)).getBookingsForBookerByState(USER_ID, BookingsRequestState.CURRENT);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsForBookerByStateWithStateFutureShouldReturnFuture() throws Exception {
        List<BookingResponseDto> bookings = Arrays.asList(bookingResponseDtoApproved, bookingResponseDtoRejected,
                bookingResponseDtoWaiting);
        when(bookingService.getBookingsForBookerByState(USER_ID, BookingsRequestState.FUTURE)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDtoApproved.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingResponseDtoApproved.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingResponseDtoApproved.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDtoApproved.getStatus().toString())))
                .andExpect(jsonPath("$[1].id", is(bookingResponseDtoRejected.getId()), Long.class))
                .andExpect(jsonPath("$[1].start", is(bookingResponseDtoRejected.getStart().toString())))
                .andExpect(jsonPath("$[1].end", is(bookingResponseDtoRejected.getEnd().toString())))
                .andExpect(jsonPath("$[1].status", is(bookingResponseDtoRejected.getStatus().toString())))
                .andExpect(jsonPath("$[2].id", is(bookingResponseDtoWaiting.getId()), Long.class))
                .andExpect(jsonPath("$[2].start", is(bookingResponseDtoWaiting.getStart().toString())))
                .andExpect(jsonPath("$[2].end", is(bookingResponseDtoWaiting.getEnd().toString())))
                .andExpect(jsonPath("$[2].status", is(bookingResponseDtoWaiting.getStatus().toString())));

        verify(bookingService, times(1)).getBookingsForBookerByState(USER_ID, BookingsRequestState.FUTURE);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsForBookerByStateWithStateWaitingShouldReturnWaiting() throws Exception {
        List<BookingResponseDto> bookings = Arrays.asList(bookingResponseDtoWaiting);
        when(bookingService.getBookingsForBookerByState(USER_ID, BookingsRequestState.WAITING)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDtoWaiting.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingResponseDtoWaiting.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingResponseDtoWaiting.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDtoWaiting.getStatus().toString())));

        verify(bookingService, times(1)).getBookingsForBookerByState(USER_ID, BookingsRequestState.WAITING);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsForBookerByStateWithStateRejectedShouldReturnRejected() throws Exception {
        List<BookingResponseDto> bookings = Arrays.asList(bookingResponseDtoRejected);
        when(bookingService.getBookingsForBookerByState(USER_ID, BookingsRequestState.REJECTED)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDtoRejected.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingResponseDtoRejected.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingResponseDtoRejected.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDtoRejected.getStatus().toString())));

        verify(bookingService, times(1)).getBookingsForBookerByState(USER_ID, BookingsRequestState.REJECTED);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsForBookerByStateWhenNoBookingsShouldReturnEmptyList() throws Exception {
        when(bookingService.getBookingsForBookerByState(USER_ID, BookingsRequestState.ALL))
                .thenReturn(Arrays.asList());

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(bookingService, times(1)).getBookingsForBookerByState(USER_ID, BookingsRequestState.ALL);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsForBookerByStateWithWrongStateShouldReturnError() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "WRONG_STATE"))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookingsForBookerByState(anyLong(), any());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingForBookerWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings", BOOKING_ID))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookingsForBookerByState(anyLong(), any());
    }

    // ==================== GET /bookings/owner ====================

    @Test
    void getBookingsForOwnerByStateWithStateAllShouldReturnAll() throws Exception {
        List<BookingResponseDto> bookings = Arrays.asList(bookingResponseDtoApproved, bookingResponseDtoRejected,
                bookingResponseDtoPast, bookingResponseDtoCurrent, bookingResponseDtoWaiting);
        when(bookingService.getBookingsForOwnerByState(USER_ID, BookingsRequestState.ALL)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));

        verify(bookingService, times(1)).getBookingsForOwnerByState(USER_ID, BookingsRequestState.ALL);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsForOwnerByStateWithStatePastShouldReturnPast() throws Exception {
        List<BookingResponseDto> bookings = Arrays.asList(bookingResponseDtoPast);
        when(bookingService.getBookingsForOwnerByState(USER_ID, BookingsRequestState.PAST)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "PAST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService, times(1)).getBookingsForOwnerByState(USER_ID, BookingsRequestState.PAST);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsForOwnerByStateWithStateCurrentShouldReturnCurrent() throws Exception {
        List<BookingResponseDto> bookings = Arrays.asList(bookingResponseDtoCurrent);
        when(bookingService.getBookingsForOwnerByState(USER_ID, BookingsRequestState.CURRENT)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService, times(1)).getBookingsForOwnerByState(USER_ID, BookingsRequestState.CURRENT);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsForOwnerByStateWithStateFutureShouldReturnFuture() throws Exception {
        List<BookingResponseDto> bookings = Arrays.asList(bookingResponseDtoApproved, bookingResponseDtoRejected,
                bookingResponseDtoWaiting);
        when(bookingService.getBookingsForOwnerByState(USER_ID, BookingsRequestState.FUTURE)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        verify(bookingService, times(1)).getBookingsForOwnerByState(USER_ID, BookingsRequestState.FUTURE);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsForOwnerByStateWithStateWaitingShouldReturnWaiting() throws Exception {
        List<BookingResponseDto> bookings = Arrays.asList(bookingResponseDtoWaiting);
        when(bookingService.getBookingsForOwnerByState(USER_ID, BookingsRequestState.WAITING)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService, times(1)).getBookingsForOwnerByState(USER_ID, BookingsRequestState.WAITING);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsForOwnerByStateWithStateRejectedShouldReturnRejected() throws Exception {
        List<BookingResponseDto> bookings = Arrays.asList(bookingResponseDtoRejected);
        when(bookingService.getBookingsForOwnerByState(USER_ID, BookingsRequestState.REJECTED)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "REJECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService, times(1)).getBookingsForOwnerByState(USER_ID, BookingsRequestState.REJECTED);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsForOwnerByStateWhenNoBookingsShouldReturnEmptyList() throws Exception {
        when(bookingService.getBookingsForOwnerByState(USER_ID, BookingsRequestState.ALL))
                .thenReturn(Arrays.asList());

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(bookingService, times(1)).getBookingsForOwnerByState(USER_ID, BookingsRequestState.ALL);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsForOwnerByStateWithWrongStateShouldReturnError() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "WRONG_STATE"))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookingsForOwnerByState(anyLong(), any());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingForOwnerWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner", BOOKING_ID))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBookingsForOwnerByState(anyLong(), Mockito.any(BookingsRequestState.class));
    }

    // ==================== POST /bookings ====================

    @Test
    void postBookingShouldCreateBooking() throws Exception {
        when(bookingService.save(eq(USER_ID), Mockito.any(BookingRequestDto.class))).thenReturn(bookingResponseDtoApproved);

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDtoApproved.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDtoApproved.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingResponseDtoApproved.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingResponseDtoApproved.getStatus().toString())));


        verify(bookingService, times(1)).save(eq(USER_ID), any());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void postBookingWithWrongItemIdShouldReturnError() throws Exception {
        bookingRequestDto.setItemId(WRONG_ID);

        when(bookingService.save(eq(USER_ID), Mockito.any(BookingRequestDto.class)))
                .thenThrow(IdNotFoundException.class);

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).save(eq(USER_ID), any());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void postBookingWithWrongUserIdShouldReturnError() throws Exception {
        when(bookingService.save(eq(WRONG_ID), Mockito.any(BookingRequestDto.class)))
                .thenThrow(IdNotFoundException.class);

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, WRONG_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).save(eq(WRONG_ID), any());
        verifyNoMoreInteractions(bookingService);
    }

    // ==================== PATCH /bookings/{bookingId} ====================

    @Test
    void approveBookingShouldApproveBooking() throws Exception {
        when(bookingService.approve(USER_ID, BOOKING_ID, true)).thenReturn(bookingResponseDtoApproved);

        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header(USER_HEADER, USER_ID)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDtoApproved.getId()), Long.class));

        verify(bookingService, times(1)).approve(USER_ID, BOOKING_ID, true);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void approveBookingShouldRejectBooking() throws Exception {
        when(bookingService.approve(eq(USER_ID), eq(BOOKING_ID), eq(false))).thenReturn(bookingResponseDtoRejected);

        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header(USER_HEADER, USER_ID)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDtoRejected.getId()), Long.class));

        verify(bookingService, times(1)).approve(USER_ID, BOOKING_ID, false);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void approveBookingWithWrongIdShouldReturnError() throws Exception {
        when(bookingService.approve(USER_ID, WRONG_ID, true)).thenThrow(IdNotFoundException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", WRONG_ID)
                        .header(USER_HEADER, USER_ID)
                        .param("approved", "true"))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).approve(USER_ID, WRONG_ID, true);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void approveBookingWithWrongUserShouldReturnError() throws Exception {
        when(bookingService.approve(WRONG_ID, BOOKING_ID, true)).thenThrow(IdNotFoundException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header(USER_HEADER, WRONG_ID)
                        .param("approved", "true"))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).approve(WRONG_ID, BOOKING_ID, true);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void approveBookingWithoutApprovedParamShouldReturnError() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).approve(anyLong(), anyLong(), anyBoolean());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void approveBookingWithWrongApprovedParamShouldReturnError() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header(USER_HEADER, USER_ID)
                        .param("approved", "test"))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).approve(anyLong(), anyLong(), anyBoolean());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void approveBookingWithoutUserHeaderShouldReturnError() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).approve(anyLong(), anyLong(), anyBoolean());
        verifyNoMoreInteractions(bookingService);
    }
}