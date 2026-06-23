package ru.practicum.shareit.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BaseClient baseClient;

    @Test
    void getShouldSendRequestWithoutUserId() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.get("/test");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate, times(1)).exchange(
                eq("/test"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void getShouldSendRequestWithUserId() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.get("/test", userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(userId));
        verify(restTemplate, times(1)).exchange(
                eq("/test"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void getShouldSendRequestWithParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 1);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.get("/test?id={id}", parameters);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate, times(1)).exchange(
                eq("/test?id={id}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                eq(parameters)
        );
    }

    @Test
    void postShouldSendRequestWithBody() {
        String requestBody = "test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.post("/test", requestBody);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getBody()).isEqualTo(requestBody);
        verify(restTemplate, times(1)).exchange(
                eq("/test"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void postShouldSendRequestWithUserIdAndBody() {
        long userId = 1L;
        String requestBody = "test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.post("/test", userId, requestBody);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getBody()).isEqualTo(requestBody);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(userId));
        verify(restTemplate, times(1)).exchange(
                eq("/test"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void putShouldSendRequestWithUserIdAndBody() {
        long userId = 1L;
        String requestBody = "test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.put("/test", userId, requestBody);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getBody()).isEqualTo(requestBody);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(userId));
        verify(restTemplate, times(1)).exchange(
                eq("/test"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void patchShouldSendRequestWithBody() {
        String requestBody = "test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.patch("/test/1", requestBody);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getBody()).isEqualTo(requestBody);
        verify(restTemplate, times(1)).exchange(
                eq("/test/1"),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void patchShouldSendRequestWithUserId() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.patch("/test/1", userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(userId));
        verify(restTemplate, times(1)).exchange(
                eq("/test/1"),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void patchShouldSendRequestWithUserIdAndParameters() {
        long userId = 1L;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 1);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), httpEntityCaptor.capture(), eq(Object.class), anyMap()))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.patch("/test/1", userId, parameters);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(userId));
        verify(restTemplate, times(1)).exchange(
                eq("/test/1"),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class),
                eq(parameters)
        );
    }

    @Test
    void patchShouldSendRequestWithUserIdAndBody() {
        long userId = 1L;
        String requestBody = "test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.patch("/test/1", userId, requestBody);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getBody()).isEqualTo(requestBody);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(userId));
        verify(restTemplate, times(1)).exchange(
                eq("/test/1"),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void deleteShouldSendRequest() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.delete("/test/1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate, times(1)).exchange(
                eq("/test/1"),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void deleteShouldSendRequestWithUserId() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.delete("/test/1", userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(userId));
        verify(restTemplate, times(1)).exchange(
                eq("/test/1"),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void deleteShouldSendRequestWithUserIdAndParameters() {
        long userId = 1L;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 1);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), httpEntityCaptor.capture(), eq(Object.class), anyMap()))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.delete("/test/1", userId, parameters);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(userId));
        verify(restTemplate, times(1)).exchange(
                eq("/test/1"),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Object.class),
                eq(parameters)
        );
    }

    @Test
    void prepareGatewayResponseWithBodyShouldReturnResponseForNot2xx() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.internalServerError().body("test");
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.get("/test");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("test");
    }

    @Test
    void prepareGatewayResponseWithoutBodyShouldReturnResponseForNot2xx() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.internalServerError().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = baseClient.get("/test");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}