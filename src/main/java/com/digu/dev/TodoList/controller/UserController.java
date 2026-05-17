package com.digu.dev.TodoList.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.digu.dev.TodoList.dto.UserDto;
import com.digu.dev.TodoList.exceptions.DuplicatedRegisteredException;
import com.digu.dev.TodoList.exceptions.ResponseError;
import com.digu.dev.TodoList.model.UserEntity;
import com.digu.dev.TodoList.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto) {
        try {
            UserEntity created = userService.create(userDto);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}").buildAndExpand(created.getId()).toUri();
            return ResponseEntity.created(location).build();
        } catch (DuplicatedRegisteredException e) {
            ResponseError conflict = ResponseError.conflict(e.getMessage());
            return ResponseEntity.status(conflict.status()).body(conflict);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable("id") String id) {
        UUID userId = UUID.fromString(id);
        Optional<UserEntity> userOptional = userService.findById(userId);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            UserDto dto = new UserDto(user.getId(), user.getUsername(), null, user.getRole());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Object> findAll(Authentication authentication) {
        List<UserEntity> users = userService.findAll();
        if (users.isEmpty()) {
            String username = authentication != null ? authentication.getName() : "Guest";
            return ResponseEntity.ok(Map.of("message", "Welcome, " + username + "! No users found."));
        }
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateById(@PathVariable("id") String id, @RequestBody UserDto userDto) {
        try {
            UUID userId = UUID.fromString(id);
            Optional<UserEntity> userOptional = userService.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            UserEntity user = userOptional.get();
            user.setUsername(userDto.username());
            if (userDto.password() != null && !userDto.password().isBlank()) {
                user.setPassword(userDto.password());
            }
            if (userDto.role() != null) {
                user.setRole(userDto.role());
            }
            userService.update(user);
            return ResponseEntity.noContent().build();
        } catch (DuplicatedRegisteredException e) {
            ResponseError conflict = ResponseError.conflict(e.getMessage());
            return ResponseEntity.status(conflict.status()).body(conflict);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") String id) {
        UUID userId = UUID.fromString(id);
        Optional<UserEntity> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.delete(userOptional.get());
        return ResponseEntity.noContent().build();
    }

}
