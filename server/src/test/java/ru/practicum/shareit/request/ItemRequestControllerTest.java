package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ItemRequestService itemRequestService;

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final long USER_ID = 1L;
    private static final long REQUEST_ID = 1L;
    private static final long WRONG_ID = 10L;

    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto2;
    private ItemRequestDto createdItemRequestDto;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();

        itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setId(1L);
        itemRequestDto1.setDescription("description1");
        itemRequestDto1.setCreated(now);

        itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(2L);
        itemRequestDto2.setDescription("description1");
        itemRequestDto2.setCreated(now.plusSeconds(3600));

        createdItemRequestDto = new ItemRequestDto();
        createdItemRequestDto.setId(3L);
        createdItemRequestDto.setDescription("Need a drill");
        createdItemRequestDto.setCreated(now);
    }

    // ==================== GET /requests ====================

    @Test
    void getItemRequestsShouldReturnAllRequests() throws Exception {
        Collection<ItemRequestDto> requests = Arrays.asList(itemRequestDto1, itemRequestDto2);
        when(itemRequestService.getItemRequests(USER_ID)).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto1.getCreated().toString())))
                .andExpect(jsonPath("$[1].id", is(itemRequestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())))
                .andExpect(jsonPath("$[1].created", is(itemRequestDto2.getCreated().toString())));

        verify(itemRequestService, times(1)).getItemRequests(USER_ID);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequestsWithoutUserHeaderShouldReturnError() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getItemRequests(anyLong());
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequestsWhenNoRequestsShouldReturnEmptyList() throws Exception {
        when(itemRequestService.getItemRequests(USER_ID)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(itemRequestService, times(1)).getItemRequests(USER_ID);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequestsWithWrongUserIdShouldReturnError() throws Exception {
        when(itemRequestService.getItemRequests(WRONG_ID)).thenThrow(IdNotFoundException.class);

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, WRONG_ID))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).getItemRequests(WRONG_ID);
        verifyNoMoreInteractions(itemRequestService);
    }

    // ==================== GET /requests/all ====================

    @Test
    void getItemRequestsOfOtherUsersShouldReturnRequests() throws Exception {
        Collection<ItemRequestDto> requests = Arrays.asList(itemRequestDto1, itemRequestDto2);
        when(itemRequestService.getOtherItemRequests(USER_ID)).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(itemRequestDto2.getId()), Long.class));

        verify(itemRequestService, times(1)).getOtherItemRequests(USER_ID);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequestsOfOtherUsersWithoutUserHeaderShouldReturnError() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getOtherItemRequests(anyLong());
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequestsOfOtherUsersWhenNoRequestsShouldReturnEmptyList() throws Exception {
        when(itemRequestService.getOtherItemRequests(USER_ID)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(itemRequestService, times(1)).getOtherItemRequests(USER_ID);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequestsOfOtherUsersWithWrongUserIdShouldReturnError() throws Exception {
        when(itemRequestService.getOtherItemRequests(WRONG_ID)).thenThrow(IdNotFoundException.class);

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, WRONG_ID))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).getOtherItemRequests(WRONG_ID);
        verifyNoMoreInteractions(itemRequestService);
    }

    // ==================== GET /requests/{requestId} ====================

    @Test
    void getItemRequestByIdShouldReturnRequest() throws Exception {
        when(itemRequestService.getItemRequest(USER_ID, REQUEST_ID)).thenReturn(itemRequestDto1);

        mockMvc.perform(get("/requests/{requestId}", REQUEST_ID)
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())));

        verify(itemRequestService, times(1)).getItemRequest(USER_ID, REQUEST_ID);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequestByIdWithoutUserHeaderShouldReturnError() throws Exception {
        mockMvc.perform(get("/requests/{requestId}", REQUEST_ID))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getItemRequest(anyLong(), anyLong());
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequestByIdWithWrongRequestIdShouldReturnError() throws Exception {
        when(itemRequestService.getItemRequest(USER_ID, WRONG_ID)).thenThrow(IdNotFoundException.class);

        mockMvc.perform(get("/requests/{requestId}", WRONG_ID)
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).getItemRequest(USER_ID, WRONG_ID);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequestsByIdWithWrongUserIdShouldReturnError() throws Exception {
        when(itemRequestService.getItemRequest(WRONG_ID, REQUEST_ID)).thenThrow(IdNotFoundException.class);

        mockMvc.perform(get("/requests/{requestId}", REQUEST_ID)
                        .header(USER_HEADER, WRONG_ID))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).getItemRequest(WRONG_ID, REQUEST_ID);
        verifyNoMoreInteractions(itemRequestService);
    }

    // ==================== POST /requests ====================

    @Test
    void postItemRequestShouldCreateRequest() throws Exception {
        when(itemRequestService.save(eq(USER_ID), Mockito.any(ItemRequestDto.class)))
                .thenReturn(itemRequestDto1);

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$.created").exists());

        verify(itemRequestService, times(1)).save(eq(USER_ID), Mockito.any(ItemRequestDto.class));
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void postItemRequestWithoutUserHeaderShouldReturnError() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto1)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).save(anyLong(), Mockito.any(ItemRequestDto.class));
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void postItemRequestWithWrongUserIdShouldReturnError() throws Exception {
        when(itemRequestService.save(eq(WRONG_ID), Mockito.any(ItemRequestDto.class)))
                .thenThrow(IdNotFoundException.class);

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, WRONG_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto1)))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).save(eq(WRONG_ID), Mockito.any(ItemRequestDto.class));
        verifyNoMoreInteractions(itemRequestService);
    }
}