package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ItemRequestDto {
    @NotBlank(message = "В запросе должно быть заполнено описание требуемой вещи!")
    private String description;
}
