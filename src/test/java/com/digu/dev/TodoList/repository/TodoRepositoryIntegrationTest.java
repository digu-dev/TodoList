package com.digu.dev.TodoList.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.digu.dev.TodoList.model.TodoEntity;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.properties.hibernate.globally_quoted_identifiers=true")
class TodoRepositoryIntegrationTest {

    @Autowired
    private TodoRepository todoRepository;

    private TodoEntity newTodo(String title) {
        TodoEntity e = new TodoEntity();
        e.setTitle(title);
        e.setDescription("A description");
        e.setCompleted(false);
        e.setDueDate(LocalDateTime.now().plusDays(1));
        return e;
    }

    @Test
    void save_persistsEntityAndGeneratesId() {
        TodoEntity saved = todoRepository.save(newTodo("Learn Spring Boot"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Learn Spring Boot");
    }

    @Test
    void findById_returnsPersistedEntity() {
        TodoEntity saved = todoRepository.save(newTodo("Task Alpha"));

        Optional<TodoEntity> found = todoRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Task Alpha");
        assertThat(found.get().getDescription()).isEqualTo("A description");
    }

    @Test
    void findById_returnsEmptyForUnknownId() {
        Optional<TodoEntity> found = todoRepository.findById(UUID.randomUUID());

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_returnsAllSavedEntities() {
        todoRepository.save(newTodo("Task One"));
        todoRepository.save(newTodo("Task Two"));

        List<TodoEntity> all = todoRepository.findAll();

        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void delete_removesEntityFromDatabase() {
        TodoEntity saved = todoRepository.save(newTodo("To Be Deleted"));

        todoRepository.delete(saved);

        assertThat(todoRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void save_updatesExistingEntity() {
        TodoEntity saved = todoRepository.save(newTodo("Original Title"));

        saved.setTitle("Updated Title");
        saved.setCompleted(true);
        todoRepository.save(saved);

        TodoEntity updated = todoRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.isCompleted()).isTrue();
    }

    @Test
    void count_reflectsNumberOfEntities() {
        long before = todoRepository.count();
        todoRepository.save(newTodo("Count Test"));

        assertThat(todoRepository.count()).isEqualTo(before + 1);
    }
}
