package com.digu.dev.TodoList.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ResponseErrorTest {

    @Test
    void patternResponse_returnsBadRequestStatus() {
        ResponseError error = ResponseError.patternResponse("bad input");

        assertThat(error.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(error.message()).isEqualTo("bad input");
        assertThat(error.fieldErrors()).isEmpty();
    }

    @Test
    void conflict_returnsConflictStatus() {
        ResponseError error = ResponseError.conflict("already exists");

        assertThat(error.status()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(error.message()).isEqualTo("already exists");
        assertThat(error.fieldErrors()).isEmpty();
    }

    @Test
    void fieldErrors_isImmutableEmptyList() {
        ResponseError error = ResponseError.patternResponse("err");

        assertThat(error.fieldErrors()).isNotNull().isEmpty();
    }
}
