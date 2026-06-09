package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long id;

    @NotBlank(message = "Должно быть заполнено имя пользователя")
    private String name;

    @NotBlank(message = "Должна быть заполнено электронная почта пользователя")
    @Email(message = "Неверный формат электронной почты")
    private String email;
}
