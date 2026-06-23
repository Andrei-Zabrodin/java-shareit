package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {
    private static final long USER_ID = 1L;
    private static final long ITEM_ID = 1L;

    @Mock
    private RestTemplate restTemplate;

    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        itemClient = new ItemClient("test", new RestTemplateBuilder());
        ReflectionTestUtils.setField(itemClient, "rest", restTemplate);
    }

    @Test
    void getItemsShouldSendRequestWithUserId() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getItems(USER_ID);

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
    void getItemShouldSendRequestWithItemId() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getItem(ITEM_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate, times(1)).exchange(
                eq("/" + ITEM_ID),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void getItemsBySearchShouldSendRequestWithText() {
        String searchText = "test";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("text", searchText);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getItemsBySearch(searchText);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate, times(1)).exchange(
                eq("/search?text={text}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                eq(parameters)
        );
    }

    @Test
    void postItemShouldSendRequestWithUserIdAndBody() {
        ItemDto requestDto = new ItemDto();
        requestDto.setName("item");
        requestDto.setDescription("description");
        requestDto.setAvailable(true);

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.postItem(requestDto, USER_ID);

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
    void patchItemShouldSendRequestWithUserIdAndItemIdAndBody() {
        ItemDto requestDto = new ItemDto();
        requestDto.setName("newItem");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.patchItem(requestDto, USER_ID, ITEM_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getBody()).isEqualTo(requestDto);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(USER_ID));
        verify(restTemplate, times(1)).exchange(
                eq("/" + ITEM_ID),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void postCommentShouldSendRequestWithUserIdAndItemIdAndBody() {
        CommentDto requestDto = new CommentDto();
        requestDto.setText("comment");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.postComment(requestDto, USER_ID, ITEM_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpEntityCaptor.getValue().getBody()).isEqualTo(requestDto);
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-Sharer-User-Id"))
                .containsExactly(String.valueOf(USER_ID));
        verify(restTemplate, times(1)).exchange(
                eq("/" + ITEM_ID + "/comment"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }
}