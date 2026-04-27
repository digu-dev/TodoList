package com.digu.dev.TodoList.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.digu.dev.TodoList.dto.TodoDto;
import com.digu.dev.TodoList.model.TodoEntity;
import com.digu.dev.TodoList.repository.TodoRepository;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private TodoDto buildDto() {
        return new TodoDto(null, "Title", "Description", false, LocalDateTime.now().plusDays(1));
    }

    private TodoEntity buildEntity(UUID id) {
        TodoEntity e = new TodoEntity();
        e.setId(id);
        e.setTitle("Title");
        e.setDescription("Description");
        e.setCompleted(false);
        e.setDueDate(LocalDateTime.now().plusDays(1));
        return e;
    }

    @Test
    void create_savesAndReturnsEntity() {
        TodoDto dto = buildDto();
        TodoEntity saved = buildEntity(UUID.randomUUID());
        when(todoRepository.save(any(TodoEntity.class))).thenReturn(saved);

        TodoEntity result = todoService.create(dto);

        assertThat(result).isEqualTo(saved);
        verify(todoRepository).save(any(TodoEntity.class));
    }

    @Test
    void create_mapsFieldsCorrectly() {
        TodoDto dto = buildDto();
        when(todoRepository.save(any(TodoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        TodoEntity result = todoService.create(dto);

        assertThat(result.getTitle()).isEqualTo(dto.title());
        assertThat(result.getDescription()).isEqualTo(dto.description());
        assertThat(result.isCompleted()).isEqualTo(dto.completed());
        assertThat(result.getDueDate()).isEqualTo(dto.dueDate());
    }

    @Test
    void update_savesEntity() {
        TodoEntity entity = buildEntity(UUID.randomUUID());
        when(todoRepository.save(entity)).thenReturn(entity);

        todoService.update(entity);

        verify(todoRepository).save(entity);
    }

    @Test
    void update_throwsWhenIdIsNull() {
        TodoEntity entity = new TodoEntity();

        assertThatThrownBy(() -> todoService.update(entity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Todo ID cannot be null for update.");
    }

    @Test
    void delete_deletesEntity() {
        TodoEntity entity = buildEntity(UUID.randomUUID());
        doNothing().when(todoRepository).delete(entity);

        todoService.delete(entity);

        verify(todoRepository).delete(entity);
    }

    @Test
    void delete_throwsWhenIdIsNull() {
        TodoEntity entity = new TodoEntity();

        assertThatThrownBy(() -> todoService.delete(entity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Todo ID cannot be null for deletion.");
    }

    @Test
    void findById_returnsEntity() {
        UUID id = UUID.randomUUID();
        TodoEntity entity = buildEntity(id);
        when(todoRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<TodoEntity> result = todoService.findById(id);

        assertThat(result).isPresent().contains(entity);
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(todoRepository.findById(id)).thenReturn(Optional.empty());

        Optional<TodoEntity> result = todoService.findById(id);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_returnsList() {
        List<TodoEntity> entities = List.of(buildEntity(UUID.randomUUID()), buildEntity(UUID.randomUUID()));
        when(todoRepository.findAll()).thenReturn(entities);

        List<TodoEntity> result = todoService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findAll_returnsEmptyList() {
        when(todoRepository.findAll()).thenReturn(List.of());

        assertThat(todoService.findAll()).isEmpty();
    }
}
