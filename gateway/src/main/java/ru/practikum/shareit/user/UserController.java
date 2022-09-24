package ru.practikum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practikum.shareit.user.dto.UserCreationDto;
import ru.practikum.shareit.user.dto.UserUpdationDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("GET запрос на получение списка всех пользователей");
        return userClient.getUsers();
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        log.info("GET запрос на получение пользлвателя с id = {}", id);
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserCreationDto userCreationDto) {
        log.info("POST запрос на создание пользователя");
        return userClient.createUser(userCreationDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserUpdationDto userUpdationDto,
                                             @PathVariable long id) {
        log.info("PATCH запрос на обновление пользователя с id = {}", id);
        return userClient.updateUser(id, userUpdationDto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable long id) {
        log.info("DELETE запрос на удаление пользователя с id = {}", id);
        return userClient.deleteUser(id);
    }
}
