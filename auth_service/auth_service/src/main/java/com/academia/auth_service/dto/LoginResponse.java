package com.academia.auth_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;   

@Data
@NoArgsConstructor  
@AllArgsConstructor

public class LoginResponse {

    private String token;
    private String run;
    private String nombre;
    private String mensaje;

}

