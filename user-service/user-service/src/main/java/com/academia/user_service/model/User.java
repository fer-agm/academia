package com.academia.user_service.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    private String run;
    private String nombre;
    private String apellido;
    private Date fecha_Nacimiento;
    private String usuario;
    private String password;
    private String correo;
    private Date fecha_Registro;

}
