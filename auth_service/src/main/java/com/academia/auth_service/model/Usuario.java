package com.academia.auth_service.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor  
@AllArgsConstructor

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
