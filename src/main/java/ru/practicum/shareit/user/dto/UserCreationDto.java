package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserCreationDto {

    private String name;

    @NotBlank(message = "E-mail не может быть пустым")
    @Email(message = "E-mail не валиден")
    private String email;
}
