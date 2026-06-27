package com.academia.auth_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academia.auth_service.dto.AuthRequest;
import com.academia.auth_service.dto.LoginResponse;
import com.academia.auth_service.dto.MessageResponse;
import com.academia.auth_service.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro y login de usuarios")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Iniciar sesión", description = "Valida run + clave y devuelve un token JWT válido por 1 hora.")
    @PostMapping("/login")
    public LoginResponse login(@RequestBody AuthRequest request) {
        String token = usuarioService.login(request.getRun(), request.getClave());
        if (token == null) {
            return new LoginResponse("error", "");
        }
        return new LoginResponse("ok", token);
    }

    @Operation(summary = "Registrar usuario", description = "Crea un usuario con run + clave.")
    @PostMapping("/registrar")
    public MessageResponse register(@RequestBody AuthRequest request) {
        String resultado = usuarioService.register(request.getRun(), request.getClave());
        return new MessageResponse(resultado);
    }
}
