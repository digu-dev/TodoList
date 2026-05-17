package com.digu.dev.TodoList.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.digu.dev.TodoList.dto.TodoDto;
import com.digu.dev.TodoList.exceptions.DuplicatedRegisteredException;
import com.digu.dev.TodoList.model.TodoEntity;
import com.digu.dev.TodoList.repository.UserRepository;
import com.digu.dev.TodoList.security.JwtAuthFilter;
import com.digu.dev.TodoList.security.JwtAuthenticationFilter;
import com.digu.dev.TodoList.security.JwtUtil;
import com.digu.dev.TodoList.security.OAuth2AuthenticationSuccessHandler;
import com.digu.dev.TodoList.security.SecurityConfig;
import com.digu.dev.TodoList.service.CustomOAuth2UserService;
import com.digu.dev.TodoList.service.JwtService;
import com.digu.dev.TodoList.service.TodoService;
import com.digu.dev.TodoList.service.UserDetailsServiceImpl;
import tools.jackson.databind.ObjectMapper;

@WithMockUser
@WebMvcTest(value = TodoController.class, excludeAutoConfiguration = OAuth2ClientWebSecurityAutoConfiguration.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtAuthenticationFilter.class})
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TodoService service;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

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
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isCreated());
    }

    @Test
    void createTodo_returns409WhenDuplicated() throws Exception {
        when(service.create(any(TodoDto.class)))
                .thenThrow(new DuplicatedRegisteredException("Todo already exists"));

        mockMvc.perform(post("/api/todos")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Todo already exists"));
    }

    @Test
    void createTodo_returns403WhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isForbidden());
    }

    // ── GET /api/todos/{id} ──────────────────────────────────────────────────────

    @Test
    void findById_returns200WithBody() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.of(buildEntity(id)));

        mockMvc.perform(get("/api/todos/{id}", id)
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void findById_returns404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/todos/{id}", id)
                        .with(user("user").roles("USER")))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/todos ───────────────────────────────────────────────────────────

    @Test
    void findAll_returns200WithList() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findAll()).thenReturn(List.of(buildEntity(id)));

        mockMvc.perform(get("/api/todos")
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void findAll_returns200WithWelcomeMessageWhenEmpty() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/todos")
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Welcome, testuser! No todos found."));
    }

    // ── PUT /api/todos/{id} ──────────────────────────────────────────────────────

    @Test
    void updateById_returns204WhenFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.of(buildEntity(id)));
        doNothing().when(service).update(any(TodoEntity.class));

        mockMvc.perform(put("/api/todos/{id}", id)
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateById_returns404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/todos/{id}", id)
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateById_returns409WhenDuplicated() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.of(buildEntity(id)));
        org.mockito.Mockito.doThrow(new DuplicatedRegisteredException("duplicate title"))
                .when(service).update(any(TodoEntity.class));

        mockMvc.perform(put("/api/todos/{id}", id)
                        .with(user("user").roles("USER"))
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

        mockMvc.perform(delete("/api/todos/{id}", id)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteById_returns404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/todos/{id}", id)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteById_returns403ForUserRole() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/todos/{id}", id)
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

}
