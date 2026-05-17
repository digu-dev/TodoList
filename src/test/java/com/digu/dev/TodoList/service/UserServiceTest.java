package com.digu.dev.TodoList.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.digu.dev.TodoList.dto.UserDto;
import com.digu.dev.TodoList.model.UserEntity;
import com.digu.dev.TodoList.model.UserRole;
import com.digu.dev.TodoList.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserDto buildDto() {
        return new UserDto(null, "john", "password123", UserRole.USER);
    }

    private UserEntity buildEntity(UUID id) {
        UserEntity e = new UserEntity();
        e.setId(id);
        e.setUsername("john");
        e.setPassword("encoded_password");
        e.setRole(UserRole.USER);
        return e;
    }

    @Test
    void create_encodesPasswordAndSavesEntity() {
        UserDto dto = buildDto();
        UserEntity saved = buildEntity(UUID.randomUUID());
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(UserEntity.class))).thenReturn(saved);

        UserEntity result = userService.create(dto);

        assertThat(result).isEqualTo(saved);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void create_mapsFieldsCorrectly() {
        UserDto dto = buildDto();
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserEntity result = userService.create(dto);

        assertThat(result.getUsername()).isEqualTo(dto.username());
        assertThat(result.getPassword()).isEqualTo("encoded_password");
        assertThat(result.getRole()).isEqualTo(dto.role());
    }

    @Test
    void update_savesEntity() {
        UserEntity entity = buildEntity(UUID.randomUUID());
        when(userRepository.save(any(UserEntity.class))).thenReturn(entity);

        userService.update(entity);

        verify(userRepository).save(entity);
    }

    @Test
    void update_throwsWhenIdIsNull() {
        UserEntity entity = new UserEntity();

        assertThatThrownBy(() -> userService.update(entity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID cannot be null for update.");
    }

    @Test
    void delete_deletesEntity() {
        UserEntity entity = buildEntity(UUID.randomUUID());
        doNothing().when(userRepository).delete(entity);

        userService.delete(entity);

        verify(userRepository).delete(entity);
    }

    @Test
    void delete_throwsWhenIdIsNull() {
        UserEntity entity = new UserEntity();

        assertThatThrownBy(() -> userService.delete(entity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID cannot be null for deletion.");
    }

    @Test
    void findById_returnsEntity() {
        UUID id = UUID.randomUUID();
        UserEntity entity = buildEntity(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<UserEntity> result = userService.findById(id);

        assertThat(result).isPresent().contains(entity);
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Optional<UserEntity> result = userService.findById(id);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_returnsList() {
        List<UserEntity> entities = List.of(buildEntity(UUID.randomUUID()), buildEntity(UUID.randomUUID()));
        when(userRepository.findAll()).thenReturn(entities);

        List<UserEntity> result = userService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findAll_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThat(userService.findAll()).isEmpty();
    }

}
