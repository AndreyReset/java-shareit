package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    @Test
    public void handleObjNotFoundExceptionTest() {
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse response = errorHandler.handleObjNotFoundException(new ObjNotFoundException("Ошибка 404"));
        assertEquals(response.getError(), "Ошибка 404");
    }

    @Test
    public void handleForbiddenExceptionTest() {
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse response = errorHandler.handleForbiddenException(new ForbiddenException("Ошибка 403"));
        assertEquals(response.getError(), "Ошибка 403");
    }

    @Test
    public void handleBadRequestException() {
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse response = errorHandler.handleBadRequestException(new BadRequestException("Ошибка 400"));
        assertEquals(response.getError(), "Ошибка 400");
    }
}