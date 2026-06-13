package com.academia.auth_service.controller;

import com.academia.auth_service.dto.LoginRequest;
import com.academia.auth_service.dto.LoginResponse;
import com.academia.auth_service.service.AuthService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j

@RestController
@RequestMapping("/auth")

public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
       log.info("[AuthController] POST /auth/login");
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validar")
    public ResponseEntity<Map<String,Boolean>> validar(@RequestHeader("Authorization") String authHeader) {
        log.info("[AuthController] GET /auth/validar");
        String token = authHeader.replace("Bearer ", "");
        boolean valido = authService.validateToken(token);
        return ResponseEntity.ok(Map.of("valido", valido));
    }
}
