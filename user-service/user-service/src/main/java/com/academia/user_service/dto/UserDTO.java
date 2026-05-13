package com.academia.user_service.dto;

import java.sql.Date;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private String id_usuario;
    private String rut;
    private String nombre;
    private String apellido;
    private String correo;
    private String usuario;
    private Date fecha_Nacimiento;
}
