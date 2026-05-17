package com.digu.dev.TodoList.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.digu.dev.TodoList.model.UserEntity;
import com.digu.dev.TodoList.model.UserRole;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.properties.hibernate.globally_quoted_identifiers=true")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private UserEntity newUser(String username, UserRole role) {
        UserEntity e = new UserEntity();
        e.setUsername(username);
        e.setPassword("encoded_password");
        e.setRole(role);
        return e;
    }

    @Test
    void save_persistsEntityAndGeneratesId() {
        UserEntity saved = userRepository.save(newUser("alice", UserRole.USER));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("alice");
        assertThat(saved.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void findById_returnsPersistedEntity() {
        UserEntity saved = userRepository.save(newUser("bob", UserRole.ADMIN));

        Optional<UserEntity> found = userRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("bob");
        assertThat(found.get().getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void findById_returnsEmptyForUnknownId() {
        Optional<UserEntity> found = userRepository.findById(UUID.randomUUID());

        assertThat(found).isEmpty();
    }

    @Test
    void findByUsername_returnsUser() {
        userRepository.save(newUser("carol", UserRole.SUPERVISOR));

        Optional<UserEntity> found = userRepository.findByUsername("carol");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("carol");
        assertThat(found.get().getRole()).isEqualTo(UserRole.SUPERVISOR);
    }

    @Test
    void findByUsername_returnsEmptyForUnknownUsername() {
        Optional<UserEntity> found = userRepository.findByUsername("unknown");

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_returnsAllSavedUsers() {
        userRepository.save(newUser("user1", UserRole.USER));
        userRepository.save(newUser("user2", UserRole.ADMIN));

        List<UserEntity> all = userRepository.findAll();

        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void delete_removesEntityFromDatabase() {
        UserEntity saved = userRepository.save(newUser("toDelete", UserRole.USER));

        userRepository.delete(saved);

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void save_updatesExistingUser() {
        UserEntity saved = userRepository.save(newUser("original", UserRole.USER));

        saved.setUsername("updated");
        saved.setRole(UserRole.ADMIN);
        userRepository.save(saved);

        UserEntity updated = userRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getUsername()).isEqualTo("updated");
        assertThat(updated.getRole()).isEqualTo(UserRole.ADMIN);
    }

}
