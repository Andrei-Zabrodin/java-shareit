package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingsRequestState;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingClient bookingClient;

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final long USER_ID = 1L;
    private static final long BOOKING_ID = 1L;
    private static final long ITEM_ID = 1L;

    private BookingRequestDto bookingRequestDto;
    private Instant future;
    private Instant past;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();
        future = now.plusSeconds(3600);
        past = now.minusSeconds(3600);

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(future);
        bookingRequestDto.setEnd(future.plusSeconds(3600));
        bookingRequestDto.setItemId(ITEM_ID);
    }

    // ==================== GET /bookings/{bookingId} ====================

    @Test
    void getBookingShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBooking(USER_ID, BOOKING_ID)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings/{bookingId}", BOOKING_ID)
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBooking(USER_ID, BOOKING_ID);
    }

    @Test
    void getBookingWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", BOOKING_ID))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBooking(anyLong(), anyLong());
    }

    // ==================== GET /bookings ====================

    @Test
    void getBookingsWithDefaultStateShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookings(USER_ID, BookingsRequestState.ALL)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookings(USER_ID, BookingsRequestState.ALL);
    }

    @Test
    void getBookingsWithStateAll_ShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookings(USER_ID, BookingsRequestState.ALL)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "all"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookings(USER_ID, BookingsRequestState.ALL);
    }

    @Test
    void getBookingsWithStatePastShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookings(USER_ID, BookingsRequestState.PAST)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "past"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookings(USER_ID, BookingsRequestState.PAST);
    }

    @Test
    void getBookingsWithStateCurrentShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookings(USER_ID, BookingsRequestState.CURRENT)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "current"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookings(USER_ID, BookingsRequestState.CURRENT);
    }

    @Test
    void getBookingsWithStateFutureShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookings(USER_ID, BookingsRequestState.FUTURE)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "future"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookings(USER_ID, BookingsRequestState.FUTURE);
    }

    @Test
    void getBookingsWithStateWaitingShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookings(USER_ID, BookingsRequestState.WAITING)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "waiting"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookings(USER_ID, BookingsRequestState.WAITING);
    }

    @Test
    void getBookingsWithStateRejectedShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookings(USER_ID, BookingsRequestState.REJECTED)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "rejected"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookings(USER_ID, BookingsRequestState.REJECTED);
    }

    @Test
    void getBookingsWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookings(anyLong(), any());
    }

    @Test
    void getBookingsWithWrongStateShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "WRONG_STATE"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookings(anyLong(), any());
    }

    // ==================== GET /bookings/owner ====================

    @Test
    void getBookingsForOwnerWithDefaultStateShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookingsForOwner(USER_ID, BookingsRequestState.ALL)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForOwner(USER_ID, BookingsRequestState.ALL);
    }

    @Test
    void getBookingsForOwnerWithStateAllShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookingsForOwner(USER_ID, BookingsRequestState.ALL)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "ALL"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForOwner(USER_ID, BookingsRequestState.ALL);
    }

    @Test
    void getBookingsForOwnerWithStatePastShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookingsForOwner(USER_ID, BookingsRequestState.PAST)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "PAST"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForOwner(USER_ID, BookingsRequestState.PAST);
    }

    @Test
    void getBookingsForOwnerWithStateCurrentShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookingsForOwner(USER_ID, BookingsRequestState.CURRENT)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForOwner(USER_ID, BookingsRequestState.CURRENT);
    }

    @Test
    void getBookingsForOwnerWithStateFutureShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookingsForOwner(USER_ID, BookingsRequestState.FUTURE)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForOwner(USER_ID, BookingsRequestState.FUTURE);
    }

    @Test
    void getBookingsForOwnerWithStateWaitingShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookingsForOwner(USER_ID, BookingsRequestState.WAITING)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "WAITING"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForOwner(USER_ID, BookingsRequestState.WAITING);
    }

    @Test
    void getBookingsForOwnerWithStateRejectedShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookingsForOwner(USER_ID, BookingsRequestState.REJECTED)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "REJECTED"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForOwner(USER_ID, BookingsRequestState.REJECTED);
    }

    @Test
    void getBookingsForOwner_WithLowerCaseState_ShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookingsForOwner(USER_ID, BookingsRequestState.ALL)).thenReturn(expectedResponse);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "all"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForOwner(USER_ID, BookingsRequestState.ALL);
    }

    @Test
    void getBookingsForOwnerWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingsForOwner(anyLong(), any());
    }

    @Test
    void getBookingsForOwnerWithWrongStateShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, USER_ID)
                        .param("state", "WRONG_STATE"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingsForOwner(anyLong(), any());
    }

    // ==================== POST /bookings ====================

    @Test
    void bookItemShouldReturnCreated() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).build();
        when(bookingClient.bookItem(eq(USER_ID), any(BookingRequestDto.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isCreated());

        verify(bookingClient, times(1)).bookItem(eq(USER_ID), any(BookingRequestDto.class));
    }

    @Test
    void bookItemWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(anyLong(), any(BookingRequestDto.class));
    }

    @Test
    void bookItemWithNullStartShouldReturnBadRequest() throws Exception {
        bookingRequestDto.setStart(null);

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(anyLong(), any(BookingRequestDto.class));
    }

    @Test
    void bookItemWithNullEndShouldReturnBadRequest() throws Exception {
        bookingRequestDto.setEnd(null);

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(anyLong(), any(BookingRequestDto.class));
    }

    @Test
    void bookItemWithNullItemIdShouldReturnBadRequest() throws Exception {
        bookingRequestDto.setItemId(null);

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(anyLong(), any(BookingRequestDto.class));
    }

    @Test
    void bookItemWithStartInPastShouldReturnBadRequest() throws Exception {
        bookingRequestDto.setStart(past);

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(anyLong(), any(BookingRequestDto.class));
    }

    @Test
    void bookItemWithEndInPastShouldReturnBadRequest() throws Exception {
        bookingRequestDto.setEnd(past);

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(anyLong(), any(BookingRequestDto.class));
    }

    // ==================== PATCH /bookings/{bookingId} ====================

    @Test
    void approveBookingWithApprovedTrueShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.approveBooking(USER_ID, BOOKING_ID, true)).thenReturn(expectedResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header(USER_HEADER, USER_ID)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).approveBooking(USER_ID, BOOKING_ID, true);
    }

    @Test
    void approveBookingWithApprovedFalseShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.approveBooking(USER_ID, BOOKING_ID, false)).thenReturn(expectedResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header(USER_HEADER, USER_ID)
                        .param("approved", "false"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).approveBooking(USER_ID, BOOKING_ID, false);
    }

    @Test
    void approveBookingWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void approveBookingWithoutApprovedParamShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void approveBookingWithWrongApprovedParamShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", BOOKING_ID)
                        .header(USER_HEADER, USER_ID)
                        .param("approved", "test"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }
}