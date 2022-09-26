package ru.practikum.shareit.booking.dto;

import java.util.Optional;

public enum BookingStateForFind {
    ALL,        //все
    CURRENT,    //текущие
    PAST,       //завершенные
    FUTURE,     //будущие
    WAITING,    //ожидающие подтверждения
    REJECTED,    //отклоненные
    UNSUPPORTED_STATUS;

    public static Optional<BookingStateForFind> from(String stringState) {
        for (BookingStateForFind state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
