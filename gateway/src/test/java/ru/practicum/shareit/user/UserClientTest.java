package ru.practicum.shareit.user;

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


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserClientTest {
    private static final long USER_ID = 1L;

    @Mock
    private RestTemplate restTemplate;

    private UserClient userClient;

    @BeforeEach
    void setUp() {
        userClient = new UserClient("test", new RestTemplateBuilder());
        ReflectionTestUtils.setField(userClient, "rest", restTemplate);
    }

    @Test
    void getUsersShouldSendRequest() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.getUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate, times(1)).exchange(
                eq(""),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void getUserShouldSendRequestWithUserId() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.getUser(USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate, times(1)).exchange(
                eq("/" + USER_ID),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void postUserShouldSendRequestWithBody() throws Exception {
        UserDto requestDto = new UserDto();
        requestDto.setName("test");
        requestDto.setEmail("test@mail.ru");

        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.postUser(requestDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getBody()).isEqualTo(requestDto);
        verify(restTemplate, times(1)).exchange(
                eq(""),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void patchUserShouldSendRequestWithUserIdAndBody() {
        UserDto requestDto = new UserDto();
        requestDto.setName("newUser");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.patchUser(requestDto, USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getBody()).isEqualTo(requestDto);
        verify(restTemplate, times(1)).exchange(
                eq("/" + USER_ID),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void deleteUserShouldSendRequestWithUserId() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.deleteUser(USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate, times(1)).exchange(
                eq("/" + USER_ID),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }
}