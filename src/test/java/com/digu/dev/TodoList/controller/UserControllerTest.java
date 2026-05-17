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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.digu.dev.TodoList.dto.UserDto;
import com.digu.dev.TodoList.exceptions.DuplicatedRegisteredException;
import com.digu.dev.TodoList.model.UserEntity;
import com.digu.dev.TodoList.model.UserRole;
import com.digu.dev.TodoList.repository.UserRepository;
import com.digu.dev.TodoList.security.JwtAuthFilter;
import com.digu.dev.TodoList.security.JwtAuthenticationFilter;
import com.digu.dev.TodoList.security.JwtUtil;
import com.digu.dev.TodoList.security.OAuth2AuthenticationSuccessHandler;
import com.digu.dev.TodoList.security.SecurityConfig;
import com.digu.dev.TodoList.service.CustomOAuth2UserService;
import com.digu.dev.TodoList.service.JwtService;
import com.digu.dev.TodoList.service.UserDetailsServiceImpl;
import com.digu.dev.TodoList.service.UserService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = OAuth2ClientWebSecurityAutoConfiguration.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtAuthenticationFilter.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

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

    private UserEntity buildEntity(UUID id) {
        UserEntity e = new UserEntity();
        e.setId(id);
        e.setUsername("john");
        e.setPassword("encoded_pw");
        e.setRole(UserRole.USER);
        return e;
    }

    private UserDto buildDto() {
        return new UserDto(null, "john", "password123", UserRole.USER);
    }

    // ── POST /api/users (public) ─────────────────────────────────────────────────

    @Test
    void createUser_returns201WhenUnauthenticated() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.create(any(UserDto.class))).thenReturn(buildEntity(id));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isCreated());
    }

    @Test
    void createUser_returns409WhenDuplicated() throws Exception {
        when(userService.create(any(UserDto.class)))
                .thenThrow(new DuplicatedRegisteredException("Username already exists"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    // ── GET /api/users/{id} ──────────────────────────────────────────────────────

    @Test
    void findById_returns200ForAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.findById(id)).thenReturn(Optional.of(buildEntity(id)));

        mockMvc.perform(get("/api/users/{id}", id)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));
    }

    @Test
    void findById_returns200ForSupervisor() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.findById(id)).thenReturn(Optional.of(buildEntity(id)));

        mockMvc.perform(get("/api/users/{id}", id)
                        .with(user("supervisor").roles("SUPERVISOR")))
                .andExpect(status().isOk());
    }

    @Test
    void findById_returns403ForUserRole() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/users/{id}", id)
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void findById_returns404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", id)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/users ───────────────────────────────────────────────────────────

    @Test
    void findAll_returns200WithListForAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.findAll()).thenReturn(List.of(buildEntity(id)));

        mockMvc.perform(get("/api/users")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john"));
    }

    @Test
    void findAll_returns200WithWelcomeMessageWhenEmpty() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users")
                        .with(user("adminuser").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Welcome, adminuser! No users found."));
    }

    @Test
    void findAll_returns403ForUserRole() throws Exception {
        mockMvc.perform(get("/api/users")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    // ── PUT /api/users/{id} ──────────────────────────────────────────────────────

    @Test
    void updateById_returns204ForAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.findById(id)).thenReturn(Optional.of(buildEntity(id)));
        doNothing().when(userService).update(any(UserEntity.class));

        mockMvc.perform(put("/api/users/{id}", id)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateById_returns403ForUserRole() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(put("/api/users/{id}", id)
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateById_returns404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/{id}", id)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDto())))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/users/{id} ───────────────────────────────────────────────────

    @Test
    void deleteById_returns204ForAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.findById(id)).thenReturn(Optional.of(buildEntity(id)));
        doNothing().when(userService).delete(any(UserEntity.class));

        mockMvc.perform(delete("/api/users/{id}", id)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteById_returns403ForSupervisor() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/users/{id}", id)
                        .with(user("supervisor").roles("SUPERVISOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteById_returns403ForUserRole() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/users/{id}", id)
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteById_returns404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/{id}", id)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

}
