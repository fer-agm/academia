package com.academia.auth_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academia.auth_service.dto.AuthRequest;
import com.academia.auth_service.dto.LoginResponse;
import com.academia.auth_service.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Inicio de sesión y emisión de tokens JWT. Los usuarios se crean en user-service.")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Iniciar sesión", description = "Valida run + clave contra los usuarios registrados y devuelve un token JWT válido por 8 horas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Petición procesada. status=ok con token JWT si las credenciales son válidas; status=error y token vacío si no lo son"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (run o clave en blanco)")
    })
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody AuthRequest request) {
        String token = usuarioService.login(request.getRun(), request.getClave());
        if (token != null) {
            return new LoginResponse("ok", token, "Inicio de sesión exitoso");
        }
        if (!usuarioService.existeUsuario(request.getRun())) {
            return new LoginResponse("error", "", "Usuario no encontrado");
        }
        return new LoginResponse("error", "", "Clave incorrecta");
    }
}
