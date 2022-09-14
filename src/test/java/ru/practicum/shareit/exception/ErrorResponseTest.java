package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    public void whenError_thenOk() {
        ErrorResponse errorResponse = new ErrorResponse("Ошибка");
        assertEquals(errorResponse.getError(), "Ошибка");
    }
}