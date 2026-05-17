package com.digu.dev.TodoList.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
@Table(name = "app_users", uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_provider_provider_id", columnNames = {"provider", "providerId"})
})
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String name;

    private String email;

    private String provider;

    private String providerId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
