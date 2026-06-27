package com.academia.user_service.model;

import java.sql.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String run; 
    
    private String nombre;
    private String apellido;
    private Date fecha_Nacimiento;
    
    @Column(unique = true)
    private String usuario;
    
    private String clave; 
    
    @Column(unique = true)
    private String email;
    
    private Date fecha_Registro;
}