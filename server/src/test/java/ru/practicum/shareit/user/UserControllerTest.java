package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private UserService userService;

    private UserDto userDto1;
    private UserDto userDto2;
    private UserDto updatedUserDto;

    @BeforeEach
    void setUp() {
        userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setName("user1");
        userDto1.setEmail("user1@mail.ru");

        userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("user2");
        userDto2.setEmail("user2@mail.ru");

        updatedUserDto = new UserDto();
        updatedUserDto.setId(1L);
        updatedUserDto.setName("newUser");
        updatedUserDto.setEmail("newUser@mail.ru");
    }

    @Test
    void getUsersShouldReturnAllUsers() throws Exception {
        Collection<UserDto> users = Arrays.asList(userDto1, userDto2);
        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto1.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto1.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())));

        verify(userService, times(1)).getUsers();
    }

    @Test
    void getUsersWhenNoUsersShouldReturnEmptyList() throws Exception {
        when(userService.getUsers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService, times(1)).getUsers();
    }

    @Test
    void getUserByIdShouldReturnUser() throws Exception {
        long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(userDto1);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getUserByIdWithWrongIdShouldReturnError() throws Exception {
        long userId = 5L;
        when(userService.getUserById(userId)).thenThrow(IdNotFoundException.class);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void postUserShouldCreateUser() throws Exception {
        when(userService.save(any(UserDto.class))).thenReturn(userDto1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));

        verify(userService, times(1)).save(any(UserDto.class));
    }

    @Test
    void patchUserShouldUpdateUser() throws Exception {
        long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("newUser");
        userDto.setEmail("newUser@mail.ru");

        when(userService.update(any(UserDto.class), anyLong())).thenReturn(updatedUserDto);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updatedUserDto.getEmail())));

        verify(userService, times(1)).update(any(UserDto.class), eq(userId));
    }

    @Test
    void patchUserWithWrongIdShouldReturnError() throws Exception {
        long userId = 5L;
        when(userService.update(any(UserDto.class), anyLong()))
                .thenThrow(IdNotFoundException.class);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto1)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).update(any(UserDto.class), eq(userId));
    }

    @Test
    void deleteUserShouldDeleteUser() throws Exception {
        long userId = 1L;
        doNothing().when(userService).delete(userId);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(userId);
    }

    @Test
    void deleteUserWithWrongIdShouldReturnError() throws Exception {
        long userId = 5L;
        doThrow(IdNotFoundException.class).when(userService).delete(userId);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).delete(userId);
    }
}
