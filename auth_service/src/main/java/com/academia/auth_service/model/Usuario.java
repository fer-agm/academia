package com.academia.auth_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Lee la tabla 'usuarios' (única fuente de verdad, administrada por user-service).
// auth-service solo valida credenciales y emite el token, no crea ni modifica usuarios.
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor  
@AllArgsConstructor

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;



    @Column(nullable = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String run;

    @Column(nullable = false)
    private String clave;

    @Column(nullable = true)
    private String nombre;
    
    @Column(nullable = true)
    private String apellido;
    

  
}
