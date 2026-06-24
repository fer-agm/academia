package com.academia.auth_service.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor  
@AllArgsConstructor

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String run;

    @Column(nullable = false)
    private String clave;

    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String apellido;
    

  
}
