package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название вещи должно быть заполнено!")
    private String name;

    @NotBlank(message = "Описание вещи должно быть заполнено!")
    private String description;

    @NotNull(message = "Необходимо указать, доступна ли вещь!")
    private Boolean available;

    private Collection<CommentDto> commentCollection;
}