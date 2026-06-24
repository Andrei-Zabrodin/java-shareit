package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateObjectException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setName("testUser");
        userDto.setEmail("test@mail.ru");
    }

    @Test
    void saveShouldCreateUser() {
        UserDto savedUser = userService.save(userDto);

        User user = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(user.getName()).isEqualTo(userDto.getName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void getUserByIdShouldReturnUser() {
        UserDto savedUser = userService.save(userDto);

        UserDto foundUser = userService.getUserById(savedUser.getId());

        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.getName()).isEqualTo(userDto.getName());
        assertThat(foundUser.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void getUsersShouldReturnAllUsers() {
        userService.save(userDto);

        UserDto anotherUser = new UserDto();
        anotherUser.setName("testUser2");
        anotherUser.setEmail("test2@mail.ru");
        userService.save(anotherUser);

        Collection<UserDto> users = userService.getUsers();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserDto::getName)
                .containsExactlyInAnyOrder("testUser", "testUser2");
    }

    @Test
    void updateShouldUpdateUser() {
        UserDto savedUser = userService.save(userDto);

        UserDto updateDto = new UserDto();
        updateDto.setName("newName");
        updateDto.setEmail("newMail@mail.ru");

        UserDto updatedUser = userService.update(updateDto, savedUser.getId());

        assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
        assertThat(updatedUser.getName()).isEqualTo(updateDto.getName());
        assertThat(updatedUser.getEmail()).isEqualTo(updateDto.getEmail());

        User user = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(user.getName()).isEqualTo(updateDto.getName());
        assertThat(user.getEmail()).isEqualTo(updateDto.getEmail());
    }

    @Test
    void updateWithDuplicateEmailShouldThrowException() {
        userService.save(userDto);

        UserDto newUser = new UserDto();
        newUser.setName("newUser");
        newUser.setEmail("another@mail.ru");
        UserDto savedUser = userService.save(newUser);

        UserDto updateDto = new UserDto();
        updateDto.setEmail("test@mail.ru");

        assertThatThrownBy(() -> userService.update(updateDto, savedUser.getId()))
                .isInstanceOf(DuplicateObjectException.class)
                .hasMessageContaining("Адрес почты test@mail.ru уже используется!");
    }

    @Test
    void deleteShouldDeleteUser() {
        UserDto savedUser = userService.save(userDto);

        userService.delete(savedUser.getId());

        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }
}