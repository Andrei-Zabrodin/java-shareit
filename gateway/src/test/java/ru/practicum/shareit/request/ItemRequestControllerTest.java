package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ItemRequestClient itemRequestClient;

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final long USER_ID = 1L;
    private static final long REQUEST_ID = 1L;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");
    }

    // ==================== GET /requests ====================

    @Test
    void getItemRequestsShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemRequestClient.getRequests(USER_ID)).thenReturn(expectedResponse);

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk());

        verify(itemRequestClient, times(1)).getRequests(USER_ID);
    }

    @Test
    void getItemRequestsWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequests(anyLong());
    }

    // ==================== GET /requests/all ====================

    @Test
    void getItemRequestsOfOtherUsersShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemRequestClient.getRequestsOfOtherUsers(USER_ID)).thenReturn(expectedResponse);

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk());

        verify(itemRequestClient, times(1)).getRequestsOfOtherUsers(USER_ID);
    }

    @Test
    void getItemRequestsOfOtherUsersWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequestsOfOtherUsers(anyLong());
    }

    // ==================== GET /requests/{requestId} ====================

    @Test
    void getItemRequestsByIdShouldReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(itemRequestClient.getRequestById(USER_ID, REQUEST_ID)).thenReturn(expectedResponse);

        mockMvc.perform(get("/requests/{requestId}", REQUEST_ID)
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk());

        verify(itemRequestClient, times(1)).getRequestById(USER_ID, REQUEST_ID);
    }

    @Test
    void getItemRequestsByIdWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/{requestId}", REQUEST_ID))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequestById(anyLong(), anyLong());
    }

    // ==================== POST /requests ====================

    @Test
    void postItemRequestShouldReturnCreated() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).build();
        when(itemRequestClient.postItemRequest(eq(USER_ID), any(ItemRequestDto.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isCreated());

        verify(itemRequestClient, times(1)).postItemRequest(eq(USER_ID), any(ItemRequestDto.class));
    }

    @Test
    void postItemRequestWithoutUserHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).postItemRequest(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    void postItemRequest_WithEmptyDescription_ShouldReturnBadRequest() throws Exception {
        itemRequestDto.setDescription("");

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).postItemRequest(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    void postItemRequestWithNullDescriptionShouldReturnBadRequest() throws Exception {
        itemRequestDto.setDescription(null);

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).postItemRequest(anyLong(), any(ItemRequestDto.class));
    }
}