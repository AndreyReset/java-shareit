package ru.practicum.shareit.requests.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    public void testDto() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(
                1L,
                "Description",
                new User(),
                LocalDateTime.of(2022, 9, 11, 15, 50),
                new ArrayList<>()
        );

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result)
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result)
                .extractingJsonPathStringValue("$.created").isEqualTo("2022-09-11T15:50:00");
    }
}