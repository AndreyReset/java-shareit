package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserUpdationDto {

    private String name;

    @Email(message = "E-mail не валиден")
    private String email;
}
