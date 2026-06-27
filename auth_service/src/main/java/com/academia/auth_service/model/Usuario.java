package com.academia.auth_service.model;


import jakarta.persistence.*;
import lombok.*;

/**
 * Credentials store for authentication only.
 * Profile data (email, nombre, apellido, ...) is owned by user-service.
 * The shared key across services is {@code run}.
 */
@Entity
@Table(name = "auth_usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String run;

    @Column(nullable = false)
    private String clave;
}
