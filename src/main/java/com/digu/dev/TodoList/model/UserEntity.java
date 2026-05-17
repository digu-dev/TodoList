package com.digu.dev.TodoList.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@ToString
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_provider_provider_id", columnNames = {"provider", "providerId"})
})
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String email;

    private String provider;

    private String providerId;

    private LocalDateTime createdAt;
}
