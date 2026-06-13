package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class CommentDto {
    private Long id;

    @NotBlank(message = "Комментарий не может быть пустым!")
    private String text;

    private String authorName;
    private LocalDateTime created;
}

