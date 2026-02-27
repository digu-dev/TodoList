package com.digu.dev.TodoList.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digu.dev.TodoList.dto.TodoDto;
import com.digu.dev.TodoList.model.TodoEntity;
import com.digu.dev.TodoList.repository.TodoRepository;

@Service
public class TodoService {
    @Autowired
    TodoRepository todoRepository;


        public TodoEntity create (TodoDto todoDto) {
        TodoEntity todo = new TodoEntity();
        todo.setTitle(todoDto.title());
        todo.setDescription(todoDto.description());
        todo.setCompleted(todoDto.completed());
        todo.setDueDate(todoDto.dueDate());
        return todoRepository.save(todo);
    }

    public void update (TodoEntity todo) {
        if(todo.getId() == null) {
            throw new IllegalArgumentException("Todo ID cannot be null for update.");
        }
        todoRepository.save(todo);
    }

    public void delete (TodoEntity todo) {
        if(todo.getId() == null) {
            throw new IllegalArgumentException("Todo ID cannot be null for deletion.");
        }
        todoRepository.delete(todo);
    }

    public Optional<TodoEntity> findById (UUID id) {return todoRepository.findById(id);}

    public List<TodoEntity> findAll () {
        return todoRepository.findAll();
    }
}
