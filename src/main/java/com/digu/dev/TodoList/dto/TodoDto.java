package com.digu.dev.TodoList.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.digu.dev.TodoList.model.TodoEntity;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

public record TodoDto(UUID id, 
   @NotBlank(message = "Title is required") 
   String title, 
   @NotBlank(message = "Description is required") 
   String description,
   @NotBlank(message = "Completed status is required") 
   boolean completed,
   @NotBlank(message = "Due date is required") 
   @Future(message = "Due date must be in the future")
   LocalDateTime dueDate) {

   public TodoEntity mapToTodoEntity() {
      TodoEntity todoEntity = new TodoEntity();
      todoEntity.setId(this.id);
      todoEntity.setTitle(this.title);
      todoEntity.setDescription(this.description);  
      todoEntity.setCompleted(this.completed);
      todoEntity.setDueDate(this.dueDate);
      return todoEntity;
    }

}
