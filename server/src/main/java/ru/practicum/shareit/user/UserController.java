package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.UserMapper.toDto;
import static ru.practicum.shareit.user.UserMapper.toUser;

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
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("POST запрос на создание пользователя");
        return toDto(userService.create(toUser(userDto)));
    }

    @PatchMapping("{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable long id) {
        log.info("PATCH запрос на обновление пользователя с id = " + id);
        return toDto(userService.update(toUser(userDto), id));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        log.info("DELETE запрос на удаление пользователя с id = " + id);
        userService.delete(id);
    }
}
