package ru.practikum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practikum.shareit.client.BaseClient;
import ru.practikum.shareit.user.dto.UserCreationDto;
import ru.practikum.shareit.user.dto.UserUpdationDto;


@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(serverUrl, builder, API_PREFIX);
    }

    public ResponseEntity<Object> getUsers() {
        return get("");
    }

    public ResponseEntity<Object> getUser(long id) {
        return get("/" + id, id);
    }

    public ResponseEntity<Object> createUser(UserCreationDto userCreationDto) {
        return post("", userCreationDto);
    }

    public ResponseEntity<Object> updateUser(long id, UserUpdationDto userUpdationDto) {
        return patch("/" + id, id, userUpdationDto);
    }

    public ResponseEntity<Object> deleteUser(long id) {
        return delete("/" + id, id);
    }
}
