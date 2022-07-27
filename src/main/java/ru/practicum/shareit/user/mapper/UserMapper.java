package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdationDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        return userDto;
    }

    public static User toUser(UserCreationDto userCreationDto) {
        User user = new User();
        user.setEmail(userCreationDto.getEmail());
        user.setName(userCreationDto.getName());
        return user;
    }

    public static User toUser(UserUpdationDto userUpdationDto) {
        User user = new User();
        user.setEmail(userUpdationDto.getEmail());
        user.setName(userUpdationDto.getName());
        return user;
    }
}
