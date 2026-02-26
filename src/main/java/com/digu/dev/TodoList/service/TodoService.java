package com.digu.dev.TodoList.service;

import org.springframework.stereotype.Service;

import com.digu.dev.TodoList.dto.TodoDto;
import com.digu.dev.TodoList.model.TodoEntity;

@Service
public class TodoService {


    public TodoDto mapToTodoDto(TodoEntity todo) {
        return new TodoDto(todo.getId(), todo.getTitle(), todo.getDescription(), todo.isCompleted(), todo.getDueDate());
    }
}
