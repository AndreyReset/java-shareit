package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemCreationDto {

    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;

    @NotBlank(message = "Описание вещи не может быть пустым")
    private String description;

    @NotNull
    @AssertTrue(message = "Изначально вещь должна быть доступна")
    private boolean available;

    private long requestId;

    private long owner;
}
