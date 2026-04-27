package com.digu.dev.TodoList.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.digu.dev.TodoList.dto.TodoDto;
import com.digu.dev.TodoList.exceptions.DuplicatedRegisteredException;
import com.digu.dev.TodoList.model.TodoEntity;
import com.digu.dev.TodoList.service.TodoService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TodoService service;

    private TodoEntity buildEntity(UUID id) {
        TodoEntity e = new TodoEntity();
        e.setId(id);
        e.setTitle("Test Task");
        e.setDescription("Test Description");
        e.setCompleted(false);
        e.setDueDate(LocalDateTime.now().plusDays(1));
        return e;
    }

    private TodoDto buildDto() {
        return new TodoDto(null, "Test Task", "Test Description", false, LocalDateTime.now().plusDays(1));
    }

    // ── POST /api/todos ──────────────────────────────────────────────────────────

    @Test
    void createTodo_returns201Created() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.create(any(TodoDto.class))).thenReturn(buildEntity(id));

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isCreated());
    }

    @Test
    void createTodo_returns409WhenDuplicated() throws Exception {
        when(service.create(any(TodoDto.class)))
                .thenThrow(new DuplicatedRegisteredException("Todo already exists"));

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Todo already exists"));
    }

    // ── GET /api/todos/{id} ──────────────────────────────────────────────────────

    @Test
    void findById_returns200WithBody() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.of(buildEntity(id)));

        mockMvc.perform(get("/api/todos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void findById_returns404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/todos/{id}", id))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/todos ───────────────────────────────────────────────────────────

    @Test
    void findAll_returns200WithList() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findAll()).thenReturn(List.of(buildEntity(id)));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void findAll_returns404WhenEmpty() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/todos/{id} ──────────────────────────────────────────────────────

    @Test
    void updateById_returns204WhenFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.of(buildEntity(id)));
        doNothing().when(service).update(any(TodoEntity.class));

        mockMvc.perform(put("/api/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateById_returns404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateById_returns409WhenDuplicated() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.of(buildEntity(id)));
        when(service.findById(id)).thenReturn(Optional.of(buildEntity(id)));
        doNothing().when(service).update(any(TodoEntity.class));

        // Simulate DuplicatedRegisteredException thrown by update
        org.mockito.Mockito.doThrow(new DuplicatedRegisteredException("duplicate title"))
                .when(service).update(any(TodoEntity.class));

        mockMvc.perform(put("/api/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isConflict());
    }

    // ── DELETE /api/todos/{id} ───────────────────────────────────────────────────

    @Test
    void deleteById_returns204WhenFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.of(buildEntity(id)));
        doNothing().when(service).delete(any(TodoEntity.class));

        mockMvc.perform(delete("/api/todos/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteById_returns404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/todos/{id}", id))
                .andExpect(status().isNotFound());
    }
}
