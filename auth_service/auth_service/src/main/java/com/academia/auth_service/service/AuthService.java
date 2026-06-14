package com.academia.auth_service.service;

import org.springframework.stereotype.Service;

import com.academia.auth_service.dto.LoginRequest;
import com.academia.auth_service.dto.LoginResponse;
import com.academia.auth_service.model.Usuario;
import com.academia.auth_service.repository.UsuarioRepository;
import com.academia.auth_service.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        log.info("Iniciando sesión con run: {}", request.getRun());

        Usuario usuario = usuarioRepository.findByRun(request.getRun())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getClave().equals(request.getClave())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(usuario.getRun());
        log.info("Inicio de sesión exitoso para run: {}", request.getRun());

        return new LoginResponse(token, usuario.getRun(), usuario.getNombre(), "Inicio de sesión exitoso");
    }

    public boolean validateToken(String token) {
        log.info("Validando token: {}", token);
        boolean valido = jwtUtil.validateToken(token);
        if (!valido){
            log.warn("Token inválido: {}", token);
        } 
        return valido;
    }



}
