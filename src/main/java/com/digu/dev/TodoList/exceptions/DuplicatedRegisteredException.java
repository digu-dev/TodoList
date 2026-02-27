package com.digu.dev.TodoList.exceptions;

public class DuplicatedRegisteredException extends RuntimeException {

    public DuplicatedRegisteredException(String message) {
        super(message);
    }

}
