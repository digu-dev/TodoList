package com.digu.dev.TodoList.exceptions;

import java.util.List;

import org.springframework.http.HttpStatus;

public record ResponseError(int status, String message, List<FieldError> fieldErrors) {

    public static ResponseError patternResponse(String message){
        return new ResponseError(HttpStatus.BAD_REQUEST.value(), message, List.of());
    }

    public static ResponseError conflict(String message){
        return new ResponseError(HttpStatus.CONFLICT.value(), message, List.of());
    }

}
