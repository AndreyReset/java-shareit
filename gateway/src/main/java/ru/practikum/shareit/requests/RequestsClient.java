package ru.practikum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practikum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class RequestsClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestsClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(serverUrl, builder, API_PREFIX);
    }

    public ResponseEntity<Object> createRequest(long userId, RequestCreationDto requestCreationDto) {
        return post("", userId, requestCreationDto);
    }

    public ResponseEntity<Object> getRequests(long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> getAllRequests(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getRequest(long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}
