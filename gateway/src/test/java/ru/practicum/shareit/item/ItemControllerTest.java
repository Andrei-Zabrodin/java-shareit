package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ItemClient itemClient;

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final long USER_ID = 1L;
    private static final long ITEM_ID = 1L;

    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        commentDto = new CommentDto();
        commentDto.setText("comment");
    }

    // ==================== GET /items ====================

    @Test
    void getItemsShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemClient.getItems(USER_ID)).thenReturn(expectedResponse);

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getItems(USER_ID);
    }

    @Test
    void getItemsWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItems(anyLong());
    }

    // ==================== GET /items/{id} ====================

    @Test
    void getItemByIdShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemClient.getItem(USER_ID, ITEM_ID)).thenReturn(expectedResponse);

        mockMvc.perform(get("/items/{id}", ITEM_ID)
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getItem(USER_ID, ITEM_ID);
    }

    @Test
    void getItemByIdWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items/{id}", ITEM_ID))
                .andExpect(status().isBadRequest());
        verify(itemClient, never()).getItem(USER_ID, ITEM_ID);
    }

    // ==================== GET /items/search ====================

    @Test
    void getItemsBySearchShouldReturnOk() throws Exception {
        String searchText = "test";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemClient.getItemsBySearch(USER_ID, searchText)).thenReturn(expectedResponse);

        mockMvc.perform(get("/items/search")
                        .header(USER_HEADER, USER_ID)
                        .param("text", searchText))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getItemsBySearch(USER_ID, searchText);
    }

    @Test
    void getItemsBySearchWithNullTextShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items/search"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItemsBySearch(eq(USER_ID), anyString());
    }

    @Test
    void getItemsBySearchWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items/search"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItemsBySearch(eq(USER_ID), anyString());
    }

    // ==================== POST /items ====================

    @Test
    void postItemShouldReturnCreated() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).build();
        when(itemClient.postItem(any(ItemDto.class), eq(USER_ID))).thenReturn(expectedResponse);

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated());

        verify(itemClient, times(1)).postItem(any(ItemDto.class), eq(USER_ID));
    }

    @Test
    void postItemWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).postItem(any(ItemDto.class), anyLong());
    }

    @Test
    void postItemWithWrongNameShouldReturnBadRequest() throws Exception {
        itemDto.setName("");

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).postItem(any(ItemDto.class), anyLong());
    }

    @Test
    void postItemWithWrongDescriptionShouldReturnBadRequest() throws Exception {
        itemDto.setDescription("");

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).postItem(any(ItemDto.class), anyLong());
    }

    @Test
    void postItemWithNullAvailableShouldReturnBadRequest() throws Exception {
        itemDto.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).postItem(any(ItemDto.class), anyLong());
    }

    @Test
    void postItemWithNullRequestIdShouldReturnOk() throws Exception {
        itemDto.setRequestId(null);

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).postItem(any(ItemDto.class), anyLong());
    }

    // ==================== PATCH /items/{id} ====================

    @Test
    void patchItemShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemClient.patchItem(any(ItemDto.class), eq(USER_ID), eq(ITEM_ID))).thenReturn(expectedResponse);

        mockMvc.perform(patch("/items/{id}", ITEM_ID)
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).patchItem(any(ItemDto.class), eq(USER_ID), eq(ITEM_ID));
    }

    @Test
    void patchItemWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/items/{id}", ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).patchItem(any(ItemDto.class), anyLong(), anyLong());
    }

    @Test
    void patchItemWithEmptyBodyShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemClient.patchItem(any(ItemDto.class), eq(USER_ID), eq(ITEM_ID))).thenReturn(expectedResponse);

        mockMvc.perform(patch("/items/{id}", ITEM_ID)
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).patchItem(any(ItemDto.class), eq(USER_ID), eq(ITEM_ID));
    }

    // ==================== POST /items/{itemId}/comment ====================

    @Test
    void postCommentShouldReturnCreated() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).build();
        when(itemClient.postComment(any(CommentDto.class), eq(USER_ID), eq(ITEM_ID))).thenReturn(expectedResponse);

        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated());

        verify(itemClient, times(1)).postComment(any(CommentDto.class), eq(USER_ID), eq(ITEM_ID));
    }

    @Test
    void postCommentWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).postComment(any(CommentDto.class), anyLong(), anyLong());
    }

    @Test
    void postCommentWithWrongTextShouldReturnBadRequest() throws Exception {
        commentDto.setText("");

        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).postComment(any(CommentDto.class), anyLong(), anyLong());
    }

    @Test
    void postCommentWithNullTextShouldReturnBadRequest() throws Exception {
        commentDto.setText(null);

        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).postComment(any(CommentDto.class), anyLong(), anyLong());
    }

    @Test
    void postCommentWithEmptyBodyShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).postComment(any(CommentDto.class), anyLong(), anyLong());
    }
}