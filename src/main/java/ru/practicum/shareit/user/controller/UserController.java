package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdationDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        log.info("GET запрос на получение списка всех пользователей");
        return userService.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public Optional<UserDto> findUserById(@PathVariable long id) {
        log.info("GET запрос на получение пользлвателя с id = " + id);
        return userService.findUserById(id)
                .map(UserMapper::toDto);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserCreationDto userCreationDto) {
        log.info("POST запрос на создание пользователя");
        User user = UserMapper.toUser(userCreationDto);
        return UserMapper.toDto(userService.create(user));
    }

    @PatchMapping("{id}")
    public UserDto update(@Valid @RequestBody UserUpdationDto userUpdationDto, @PathVariable long id) {
        log.info("PATCH запрос на обновление пользователя с id = " + id);
        User user = UserMapper.toUser(userUpdationDto);
        return UserMapper.toDto(userService.update(user, id));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        log.info("DELETE запрос на удаление пользователя с id = " + id);
        userService.delete(id);
    }
}
