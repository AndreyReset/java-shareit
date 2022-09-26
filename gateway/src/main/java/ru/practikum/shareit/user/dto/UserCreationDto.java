package ru.practikum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationDto {

    private String name;

    @NotBlank(message = "E-mail не может быть пустым")
    @Email(message = "E-mail не валиден")
    private String email;
}
