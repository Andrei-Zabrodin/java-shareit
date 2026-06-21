package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ItemService itemService;

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final long OWNER_ID = 1L;
    private static final long ITEM_ID = 1L;
    private static final long WRONG_ID = 10L;
    private static final long USER_ID = 1L;

    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private ItemDto updatedItemDto;
    private CommentDto commentDto;
    private CommentDto createdCommentDto;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();

        itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("Item1");
        itemDto1.setDescription("Description1");
        itemDto1.setAvailable(true);
        itemDto1.setRequestId(1L);

        itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("Item2");
        itemDto2.setDescription("Description2");
        itemDto2.setAvailable(true);
        itemDto2.setRequestId(null);

        updatedItemDto = new ItemDto();
        updatedItemDto.setId(1L);
        updatedItemDto.setName("newItem");
        updatedItemDto.setDescription("newDescription");
        updatedItemDto.setAvailable(false);
        updatedItemDto.setRequestId(null);

        commentDto = new CommentDto();
        commentDto.setText("comment");

        createdCommentDto = new CommentDto();
        createdCommentDto.setId(1L);
        createdCommentDto.setText("comment");
        createdCommentDto.setAuthorName("user1");
        createdCommentDto.setCreated(now);
    }

    // ==================== GET /items ====================

    @Test
    void getItemsShouldReturnAllItems() throws Exception {
        Collection<ItemDto> items = Arrays.asList(itemDto1, itemDto2);
        when(itemService.getItems(OWNER_ID)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, OWNER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDto2.getAvailable())));

        verify(itemService, times(1)).getItems(OWNER_ID);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemsWithoutUserHeaderShouldReturnError() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getItems(anyLong());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemsWhenNoItemsShouldReturnEmptyList() throws Exception {
        when(itemService.getItems(OWNER_ID)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, OWNER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(itemService, times(1)).getItems(OWNER_ID);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemsWithWrongOwnerIdShouldReturnError() throws Exception {
        when(itemService.getItems(WRONG_ID)).thenThrow(IdNotFoundException.class);

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, WRONG_ID))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).getItems(WRONG_ID);
        verifyNoMoreInteractions(itemService);
    }

    // ==================== GET /items/{id} ====================

    @Test
    void getItemByIdShouldReturnItem() throws Exception {
        when(itemService.getItemById(ITEM_ID)).thenReturn(itemDto1);

        mockMvc.perform(get("/items/{id}", ITEM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable())));

        verify(itemService, times(1)).getItemById(ITEM_ID);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemByIdWithWrongIdShouldReturnError() throws Exception {
        when(itemService.getItemById(WRONG_ID)).thenThrow(IdNotFoundException.class);

        mockMvc.perform(get("/items/{id}", WRONG_ID))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).getItemById(WRONG_ID);
        verifyNoMoreInteractions(itemService);
    }

    // ==================== GET /items/search ====================

    @Test
    void getItemsBySearchShouldReturnItems() throws Exception {
        String searchText = "test";
        Collection<ItemDto> items = Arrays.asList(itemDto1, itemDto2);
        when(itemService.getItemsBySearch(searchText)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getId()), Long.class));

        verify(itemService, times(1)).getItemsBySearch(searchText);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemsBySearchWithEmptyTextShouldReturnEmptyList() throws Exception {
        String searchText = "";
        when(itemService.getItemsBySearch(searchText)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(itemService, times(1)).getItemsBySearch(searchText);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemsBySearchWithNullTextShouldReturnError() throws Exception {
        mockMvc.perform(get("/items/search"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getItemsBySearch(anyString());
        verifyNoMoreInteractions(itemService);
    }

    // ==================== POST /items ====================

    @Test
    void postItemShouldCreateItem() throws Exception {
        when(itemService.save(Mockito.any(ItemDto.class), eq(OWNER_ID))).thenReturn(itemDto1);

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, OWNER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable())));

        verify(itemService, times(1)).save(Mockito.any(ItemDto.class), eq(OWNER_ID));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void postItemWithoutUserHeaderShouldReturnError() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto1)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).save(Mockito.any(ItemDto.class), anyLong());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void postItemWithWrongOwnerIdShouldReturnError() throws Exception {
        long wrongOwnerId = WRONG_ID;
        when(itemService.save(Mockito.any(ItemDto.class), eq(wrongOwnerId))).thenThrow(IdNotFoundException.class);

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, wrongOwnerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto1)))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).save(Mockito.any(ItemDto.class), eq(wrongOwnerId));
        verifyNoMoreInteractions(itemService);
    }

    // ==================== PATCH /items/{id} ====================

    @Test
    void patchItemShouldUpdateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("newItem");
        itemDto.setDescription("newDescription");
        itemDto.setAvailable(false);

        when(itemService.update(Mockito.any(ItemDto.class), eq(OWNER_ID), eq(ITEM_ID))).thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/{id}", ITEM_ID)
                        .header(USER_HEADER, OWNER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(updatedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItemDto.getAvailable())));

        verify(itemService, times(1)).update(Mockito.any(ItemDto.class), eq(OWNER_ID), eq(ITEM_ID));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void patchItemWithoutUserHeaderShouldReturnError() throws Exception {
        mockMvc.perform(patch("/items/{id}", ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto1)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).update(Mockito.any(ItemDto.class), anyLong(), anyLong());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void patchItemWithWrongIdShouldReturnError() throws Exception {
        when(itemService.update(Mockito.any(ItemDto.class), eq(OWNER_ID), eq(WRONG_ID)))
                .thenThrow(IdNotFoundException.class);

        mockMvc.perform(patch("/items/{id}", WRONG_ID)
                        .header(USER_HEADER, OWNER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto1)))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).update(Mockito.any(ItemDto.class), eq(OWNER_ID), eq(WRONG_ID));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void patchItemWithWrongOwnerIdShouldReturnError() throws Exception {
        when(itemService.update(Mockito.any(ItemDto.class), eq(WRONG_ID), eq(ITEM_ID)))
                .thenThrow(IdNotFoundException.class);

        mockMvc.perform(patch("/items/{id}", ITEM_ID)
                        .header(USER_HEADER, WRONG_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto1)))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).update(Mockito.any(ItemDto.class), eq(WRONG_ID), eq(ITEM_ID));
        verifyNoMoreInteractions(itemService);
    }

    // ==================== POST /items/{itemId}/comment ====================

    @Test
    void postCommentShouldCreateComment() throws Exception {
        when(itemService.save(Mockito.any(CommentDto.class), eq(USER_ID), eq(ITEM_ID))).thenReturn(createdCommentDto);

        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdCommentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(createdCommentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(createdCommentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", notNullValue()));

        verify(itemService, times(1)).save(Mockito.any(CommentDto.class), eq(USER_ID), eq(ITEM_ID));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void postCommentWithoutUserHeaderShouldReturnError() throws Exception {
        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).save(Mockito.any(CommentDto.class), anyLong(), anyLong());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void postCommentWithWrongItemIdShouldReturnError() throws Exception {
        when(itemService.save(Mockito.any(CommentDto.class), eq(USER_ID), eq(WRONG_ID)))
                .thenThrow(IdNotFoundException.class);

        mockMvc.perform(post("/items/{itemId}/comment", WRONG_ID)
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).save(Mockito.any(CommentDto.class), eq(USER_ID), eq(WRONG_ID));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void postCommentWithWrongUserIdShouldReturnError() throws Exception {
        long wrongUserId = WRONG_ID;
        when(itemService.save(Mockito.any(CommentDto.class), eq(wrongUserId), eq(ITEM_ID)))
                .thenThrow(IdNotFoundException.class);

        mockMvc.perform(post("/items/{itemId}/comment", ITEM_ID)
                        .header(USER_HEADER, wrongUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).save(Mockito.any(CommentDto.class), eq(wrongUserId), eq(ITEM_ID));
        verifyNoMoreInteractions(itemService);
    }
}