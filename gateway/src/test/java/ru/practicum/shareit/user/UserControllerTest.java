package ru.practicum.shareit.user;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserClient userClient;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setName("user1");
        userDto.setEmail("user1@mail.ru");
    }

    @Test
    void getUsersShouldReturnUsersList() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(userClient.getUsers()).thenReturn(expectedResponse);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient, times(1)).getUsers();
    }

    @Test
    void getUserByIdShouldReturnUser() throws Exception {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(userClient.getUser(userId)).thenReturn(expectedResponse);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userClient, times(1)).getUser(userId);
    }

    @Test
    void getUserByIdWithWrongIdShouldReturnError() throws Exception {
        long wrongId = 5L;
        ResponseEntity<Object> errorResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(userClient.getUser(wrongId)).thenReturn(errorResponse);

        mockMvc.perform(get("/users/{id}", wrongId))
                .andExpect(status().isNotFound());

        verify(userClient, times(1)).getUser(wrongId);
    }

    @Test
    void postUserShouldCreateUser() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).build();
        when(userClient.postUser(any(UserDto.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated());

        verify(userClient, times(1)).postUser(any(UserDto.class));
    }

    @Test
    void postUser_WithValidData_ShouldReturnCreated() throws Exception {
        // Given
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).build();
        when(userClient.postUser(any(UserDto.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated());

        verify(userClient, times(1)).postUser(any(UserDto.class));
    }

    @Test
    void postUserWithWrongEmailShouldReturnBadRequest() throws Exception {
        userDto.setEmail("random-stuff");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).postUser(any(UserDto.class));
    }

    @Test
    void postUserWithBlankNameShouldReturnBadRequest() throws Exception {
        userDto.setName("");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).postUser(any(UserDto.class));
    }

    @Test
    void postUserWithNullNameShouldReturnBadRequest() throws Exception {
        userDto.setName(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).postUser(any(UserDto.class));
    }

    @Test
    void postUserWithNullEmailShouldReturnBadRequest() throws Exception {
        userDto.setEmail(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).postUser(any(UserDto.class));
    }

    @Test
    void patchUserShouldUpdateUser() throws Exception {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(userClient.patchUser(any(UserDto.class), eq(userId))).thenReturn(expectedResponse);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userClient, times(1)).patchUser(any(UserDto.class), eq(userId));
    }
/*
    @Test
    void patchUserWithPartialUpdate_ShouldUpdateUser() throws Exception {
        // Given
        long userId = 1L;
        UserDto partialUpdate = new UserDto();
        partialUpdate.setName("Updated Name Only");
        // email не указываем

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(userClient.patchUser(any(UserDto.class), eq(userId))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk());

        verify(userClient, times(1)).patchUser(any(UserDto.class), eq(userId));
    }*/

    @Test
    void patchUserWithWrongIdShouldReturnError() throws Exception {
        long userId = 5L;
        ResponseEntity<Object> errorResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        when(userClient.patchUser(any(UserDto.class), eq(userId))).thenReturn(errorResponse);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());

        verify(userClient, times(1)).patchUser(any(UserDto.class), eq(userId));
    }

    @Test
    void deleteUserShouldDeleteUser() throws Exception {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(userClient.deleteUser(userId)).thenReturn(expectedResponse);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userClient, times(1)).deleteUser(userId);
    }
}