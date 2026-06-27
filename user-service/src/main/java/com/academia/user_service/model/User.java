package com.academia.user_service.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class User {
    
    @Id
    private String id; 

    @Column(unique = true, nullable = false)
    private String run; 
    
    private String nombre;
    private String apellido;
    private Date fecha_Nacimiento;
    
    @Column(unique = true)
    private String usuario;
    
    private String clave; 
    
    private String email;
    
    private Date fecha_Registro;
}