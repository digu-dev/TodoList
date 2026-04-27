package com.digu.dev.TodoList.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.digu.dev.TodoList.model.TodoEntity;

class TodoDtoTest {

    @Test
    void mapToTodoEntity_mapsAllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        TodoDto dto = new TodoDto(id, "Buy milk", "At the grocery store", true, dueDate);

        TodoEntity entity = dto.mapToTodoEntity();

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getTitle()).isEqualTo("Buy milk");
        assertThat(entity.getDescription()).isEqualTo("At the grocery store");
        assertThat(entity.isCompleted()).isTrue();
        assertThat(entity.getDueDate()).isEqualTo(dueDate);
    }

    @Test
    void mapToTodoEntity_withNullId_setsNullId() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        TodoDto dto = new TodoDto(null, "Title", "Desc", false, dueDate);

        TodoEntity entity = dto.mapToTodoEntity();

        assertThat(entity.getId()).isNull();
    }

    @Test
    void mapToTodoEntity_completedFalse_mapsCorrectly() {
        TodoDto dto = new TodoDto(null, "Title", "Desc", false, LocalDateTime.now().plusDays(1));

        TodoEntity entity = dto.mapToTodoEntity();

        assertThat(entity.isCompleted()).isFalse();
    }

    @Test
    void record_accessors_returnCorrectValues() {
        UUID id = UUID.randomUUID();
        LocalDateTime dueDate = LocalDateTime.now().plusDays(5);
        TodoDto dto = new TodoDto(id, "My Title", "My Description", true, dueDate);

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.title()).isEqualTo("My Title");
        assertThat(dto.description()).isEqualTo("My Description");
        assertThat(dto.completed()).isTrue();
        assertThat(dto.dueDate()).isEqualTo(dueDate);
    }
}
