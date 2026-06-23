package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingsRequestState;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {
    private static final long USER_ID = 1L;
    private static final long ITEM_ID = 1L;
    private static final long BOOKING_ID = 1L;

    @Mock
    private RestTemplate restTemplate;

    private BookingClient bookingClient;

    @BeforeEach
    void setUp() {
        bookingClient = new BookingClient("test", new RestTemplateBuilder());
        ReflectionTestUtils.setField(bookingClient, "rest", restTemplate);
    }

    @Test
    void getBookingsShouldSendRequestWithState() {
        BookingsRequestState state = BookingsRequestState.WAITING;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("state", "WAITING");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class), anyMap()))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookings(USER_ID, state);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(USER_ID));
        verify(restTemplate, times(1)).exchange(
                eq("?state={state}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                eq(parameters)
        );
    }

    @Test
    void getBookingsForOwnerShouldSendRequestWithState() {
        BookingsRequestState state = BookingsRequestState.REJECTED;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("state", "REJECTED");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class), anyMap()))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookingsForOwner(USER_ID, state);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(USER_ID));
        verify(restTemplate, times(1)).exchange(
                eq("/owner?state={state}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                eq(parameters)
        );
    }

    @Test
    void bookItemShouldSendRequestWithUserIdAndBody() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setStart(Instant.now().plusSeconds(3600));
        requestDto.setEnd(Instant.now().plusSeconds(7200));
        requestDto.setItemId(ITEM_ID);

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.bookItem(USER_ID, requestDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getBody()).isEqualTo(requestDto);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(USER_ID));
        verify(restTemplate, times(1)).exchange(
                eq(""),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void getBookingShouldSendRequestWithUserIdAndBookingId() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBooking(USER_ID, BOOKING_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(USER_ID));
        verify(restTemplate, times(1)).exchange(
                eq("/" + BOOKING_ID),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void approveBookingShouldSendRequestWithUserIdAndBookingIdAndApproved() {
        boolean approved = true;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("approved", approved);

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), httpEntityCaptor.capture(), eq(Object.class), anyMap()))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.approveBooking(USER_ID, BOOKING_ID, approved);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(USER_ID));
        verify(restTemplate, times(1)).exchange(
                eq("/" + BOOKING_ID + "?approved={approved}"),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class),
                eq(parameters)
        );
    }
}