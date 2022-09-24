package ru.practikum.shareit.requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RequestCreationDto {

    @NotBlank(message = "Описание запроса не может быть пустым")
    private String description;

}
