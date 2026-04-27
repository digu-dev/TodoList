package com.digu.dev.TodoList.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DuplicatedRegisteredExceptionTest {

    @Test
    void constructor_storesMessage() {
        DuplicatedRegisteredException ex = new DuplicatedRegisteredException("Todo already exists");

        assertThat(ex.getMessage()).isEqualTo("Todo already exists");
    }

    @Test
    void isInstanceOfRuntimeException() {
        DuplicatedRegisteredException ex = new DuplicatedRegisteredException("duplicate");

        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void fieldError_storesFieldAndErrorMessage() {
        FieldError fe = new FieldError("title", "must not be blank");

        assertThat(fe.field()).isEqualTo("title");
        assertThat(fe.error()).isEqualTo("must not be blank");
    }
}
