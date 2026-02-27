package com.digu.dev.TodoList.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.digu.dev.TodoList.dto.TodoDto;
import com.digu.dev.TodoList.exceptions.DuplicatedRegisteredException;
import com.digu.dev.TodoList.exceptions.ResponseError;
import com.digu.dev.TodoList.model.TodoEntity;
import com.digu.dev.TodoList.service.TodoService;




@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoService service;


    @PostMapping
    public ResponseEntity<Object> createTodo(@RequestBody TodoDto todoDto) {
        try {
            TodoEntity todoEntity = todoDto.mapToTodoEntity();  
            service.create(todoDto);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().
            path("{id}").buildAndExpand(todoEntity.getId()).toUri();
            return ResponseEntity.created(location).build();
        } catch (DuplicatedRegisteredException e) {
            ResponseError conflict = ResponseError.conflict(e.getMessage());
            return ResponseEntity.status(conflict.status()).body(conflict);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TodoDto> findById(@PathVariable("id") String id) {
        UUID todoId = UUID.fromString(id);
        Optional<TodoEntity> todoOptional = service.findById(todoId);
        if (todoOptional.isPresent()) {
            TodoEntity todo = todoOptional.get();
            TodoDto todoDto = new TodoDto(todo.getId(),
            todo.getTitle(), 
            todo.getDescription(), 
            todo.isCompleted(), 
            todo.getDueDate());
            return ResponseEntity.ok(todoDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<TodoEntity>> findAll(){
        List<TodoEntity> todos = service.findAll();
       if(todos.isEmpty()){
        return ResponseEntity.notFound().build();
       } else {
        return ResponseEntity.ok(todos);
       }

    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateById(@PathVariable("id") String id, @RequestBody TodoDto TodoDto ) {
            try{
               UUID todoId = UUID.fromString(id);
                Optional<TodoEntity> todoOptional = service.findById(todoId);
                if(todoOptional.isEmpty()){
                    return ResponseEntity.notFound().build();
                } else {
                   TodoEntity todo = todoOptional.get();
                   todo.setTitle(TodoDto.title());
                   todo.setDescription(TodoDto.description());
                   todo.setCompleted(TodoDto.completed());
                   todo.setDueDate(TodoDto.dueDate()); 
                   service.update(todo);
                   return ResponseEntity.noContent().build();
                }
            }catch(DuplicatedRegisteredException e){
                ResponseError conflict = ResponseError.conflict(e.getMessage());
                return ResponseEntity.status(conflict.status()).body(conflict);
            }
        }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") String id) {
        UUID todoId = UUID.fromString(id);
        Optional<TodoEntity> todoOptional = service.findById(todoId);
        if(todoOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        } else {
            service.delete(todoOptional.get());
            return ResponseEntity.noContent().build();
        }
    
    





    }
}
