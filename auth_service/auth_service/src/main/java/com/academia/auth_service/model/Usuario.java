package com.academia.auth_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Date;

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
    private String password;
    private String email;

    @Column(name = "fecha_registro")
    private Date fechaRegistro;

    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

}
