package ru.practicum.shareit.requests.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ItemRequestCreationDto {

    @NotBlank(message = "Описание запроса не может быть пустым")
    private String description;

}
