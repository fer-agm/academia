package com.academia.auth_service.model;

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

public class Usuario {
    @Id
    private String id;
    private String run;
    private String nombre;
    private String apellido;
    private String usuario;
    private String clave;
    private String email;

    @Column(name = "fecha_registro")
    private Date fechaRegistro;

    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

}
