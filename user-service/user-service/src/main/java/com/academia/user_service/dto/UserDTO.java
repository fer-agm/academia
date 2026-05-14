package com.academia.user_service.dto;

import java.sql.Date;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String run;
    private String nombre;
    private String apellido;
    private Date fechaNacimiento;
    private String usuario;
    private String clave; // Opcional: eliminar en DTOs de respuesta por seguridad
    private String email;
    private Date fechaRegistro;
}

