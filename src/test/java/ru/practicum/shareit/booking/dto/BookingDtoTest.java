package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    public void testDto() throws Exception {
        BookingDto dto = new BookingDto(
                1L,
                LocalDateTime.of(2022, 9, 11, 17, 56),
                LocalDateTime.of(2022, 9, 12, 17, 56),
                BookingStatus.WAITING,
                new Item(),
                new User()
        );

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-09-11T17:56:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-09-12T17:56:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}