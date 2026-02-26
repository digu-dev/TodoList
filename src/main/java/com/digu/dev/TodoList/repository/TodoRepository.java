package com.digu.dev.TodoList.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digu.dev.TodoList.model.TodoEntity;

public interface TodoRepository extends JpaRepository<TodoEntity, UUID> {
    

}
