package ru.practikum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practikum.shareit.client.BaseClient;
import ru.practikum.shareit.item.dto.CommentAddingDto;
import ru.practikum.shareit.item.dto.ItemCreationDto;
import ru.practikum.shareit.item.dto.ItemUpdationDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(serverUrl, builder, API_PREFIX);
    }

    public ResponseEntity<Object> getItems(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItem(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> createItem(long userId, ItemCreationDto itemCreationDto) {
        return post("", userId, itemCreationDto);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemUpdationDto itemUpdationDto) {
        return patch("/" + itemId, userId, itemUpdationDto);
    }

    public ResponseEntity<Object> search(long userId, String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentAddingDto commentAdding) {
        return post("/" + itemId + "/comment", userId, commentAdding);
    }
}
