package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {
    private static final long USER_ID = 1L;
    private static final long REQUEST_ID = 1L;

    @Mock
    private RestTemplate restTemplate;

    private ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        itemRequestClient = new ItemRequestClient("test", new RestTemplateBuilder());
        ReflectionTestUtils.setField(itemRequestClient, "rest", restTemplate);
    }

    @Test
    void getRequestsShouldSendRequestWithUserId() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.getRequests(USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(USER_ID));
        verify(restTemplate, times(1)).exchange(
                eq(""),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void getRequestsOfOtherUsersShouldSendRequestWithUserId() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.getRequestsOfOtherUsers(USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(USER_ID));
        verify(restTemplate, times(1)).exchange(
                eq("/all"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void getRequestByIdShouldSendRequestWithUserIdAndRequestId() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.getRequestById(USER_ID, REQUEST_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(USER_ID));
        verify(restTemplate, times(1)).exchange(
                eq("/" + REQUEST_ID),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void postItemRequestShouldSendRequestWithUserIdAndBody() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("request");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.postItemRequest(USER_ID, requestDto);

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
}