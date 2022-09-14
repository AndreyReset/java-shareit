package ru.practicum.shareit.pageable;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.BadRequestException;

import static org.junit.jupiter.api.Assertions.*;

class OffsetLimitPageableTest {

    @Test
    public void whenFromLessZero_thenThrowException() {
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    Pageable pageable = OffsetLimitPageable.of(-1, 10);
                }
        );
        assertEquals("from must be positive and size must be more then 0", exception.getMessage());
    }

    @Test
    public void whenSizeLessZero_thenThrowException() {
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    Pageable pageable = OffsetLimitPageable.of(10, -1);
                }
        );
        assertEquals("from must be positive and size must be more then 0", exception.getMessage());
    }

    @Test
    public void whenFromAndSizeIsNull_thenFromAndSizeIsDefault() {
        Pageable pageable = OffsetLimitPageable.of(null, null);
        assertEquals(pageable.getPageSize(), 20);
        assertEquals(pageable.getOffset(), 0);
    }

    @Test
    public void whenFromLessZeroWithSort_thenThrowException() {
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    Pageable pageable = OffsetLimitPageable.of(
                            -1,
                            10,
                            Sort.by(Sort.Direction.DESC, "created"));
                }
        );
        assertEquals("from must be positive and size must be more then 0", exception.getMessage());
    }

    @Test
    public void whenSizeLessZeroWith_thenThrowException() {
        Throwable exception = assertThrows(
                BadRequestException.class,
                () -> {
                    Pageable pageable = OffsetLimitPageable.of(
                            10,
                            -1,
                            Sort.by(Sort.Direction.DESC, "created"));
                }
        );
        assertEquals("from must be positive and size must be more then 0", exception.getMessage());
    }

    @Test
    public void whenFromAndSizeIsNullWithSort_thenFromAndSizeIsDefault() {
        Pageable pageable = OffsetLimitPageable.of(null, null, Sort.by(Sort.Direction.DESC, "created"));
        assertEquals(pageable.getPageSize(), 20);
        assertEquals(pageable.getOffset(), 0);
    }

    @Test
    public void whenGetPageNumber_thenReturnZero() {
        Pageable pageable = OffsetLimitPageable.of(0, 10);
        assertEquals(pageable.getPageNumber(), 0);
    }

    @Test
    public void whenGetSort_thenOk() {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Pageable pageable = OffsetLimitPageable.of(null, null, Sort.by(Sort.Direction.DESC, "created"));
        assertEquals(pageable.getSort(), sort);
    }

    @Test
    public void whenNext_thenOk() {
        Pageable pageable = OffsetLimitPageable.of(
                1,
                10,
                Sort.by(Sort.Direction.DESC, "created"));
        Pageable pageable1 = pageable.next();
        assertEquals(pageable.getOffset()+10, pageable1.getOffset());
    }

    @Test
    public void whenPreviousOrFirst_thenOk() {
        Pageable pageable = OffsetLimitPageable.of(
                1,
                10,
                Sort.by(Sort.Direction.DESC, "created"));
        Pageable pageable1 = pageable.previousOrFirst();
        assertEquals(pageable.getOffset(), pageable1.getOffset());
    }

    @Test
    public void whenFirst_thenOk() {
        Pageable pageable = OffsetLimitPageable.of(
                1,
                10,
                Sort.by(Sort.Direction.DESC, "created"));
        Pageable pageable1 = pageable.first();
        assertEquals(pageable.getOffset(), pageable1.getOffset());
    }

    @Test
    public void whenWithPage_thenOk() {
        Pageable pageable = OffsetLimitPageable.of(
                1,
                10,
                Sort.by(Sort.Direction.DESC, "created"));
        Pageable pageable1 = pageable.withPage(2);
        assertEquals(pageable.getOffset()+10*2, pageable1.getOffset());
    }
}