package com.digu.dev.TodoList.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.digu.dev.TodoList.dto.UserDto;
import com.digu.dev.TodoList.model.UserEntity;
import com.digu.dev.TodoList.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserEntity create(UserDto dto) {
        UserEntity user = new UserEntity();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(dto.role());
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void update(UserEntity user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for update.");
        }
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void delete(UserEntity user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for deletion.");
        }
        userRepository.delete(user);
    }

    public Optional<UserEntity> findById(UUID id) {
        return userRepository.findById(id);
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

}
