package ru.practikum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentAddingDto {

    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
}
