package com.academia.auth_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.academia.auth_service.dto.AuthRequest;
import com.academia.auth_service.dto.LoginResponse;
import com.academia.auth_service.dto.MessageResponse;
import com.academia.auth_service.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private AuthController authController;

    private AuthRequest buildRequest(String run, String clave) {
        AuthRequest request = new AuthRequest();
        request.setRun(run);
        request.setClave(clave);
        return request;
    }

    // ---------- login ----------

    @Test
    void login_validCredentials_returnsOkStatusWithToken() {
        // Given
        String run = "11111111-1";
        String clave = "1234";
        AuthRequest request = buildRequest(run, clave);
        when(usuarioService.login(run, clave)).thenReturn("jwt-token");

        // When
        LoginResponse response = authController.login(request);

        // Then
        assertNotNull(response);
        assertEquals("ok", response.getStatus());
        assertEquals("jwt-token", response.getToken());
        assertEquals("Inicio de sesión exitoso", response.getMensaje());
        verify(usuarioService).login(run, clave);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void login_userNotFound_returnsErrorUsuarioNoEncontrado() {
        // Given
        String run = "00000000-0";
        String clave = "wrong";
        AuthRequest request = buildRequest(run, clave);
        when(usuarioService.login(run, clave)).thenReturn(null);
        when(usuarioService.existeUsuario(run)).thenReturn(false);

        // When
        LoginResponse response = authController.login(request);

        // Then
        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertEquals("", response.getToken());
        assertEquals("Usuario no encontrado", response.getMensaje());
        verify(usuarioService).login(run, clave);
        verify(usuarioService).existeUsuario(run);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void login_wrongClave_returnsErrorClaveIncorrecta() {
        // Given
        String run = "10492048-9";
        String clave = "malaclave";
        AuthRequest request = buildRequest(run, clave);
        when(usuarioService.login(run, clave)).thenReturn(null);
        when(usuarioService.existeUsuario(run)).thenReturn(true);

        // When
        LoginResponse response = authController.login(request);

        // Then
        assertNotNull(response);
        assertEquals("error", response.getStatus());
        assertEquals("", response.getToken());
        assertEquals("Clave incorrecta", response.getMensaje());
        verify(usuarioService).login(run, clave);
        verify(usuarioService).existeUsuario(run);
        verifyNoMoreInteractions(usuarioService);
    }

    // ---------- register ----------

    @Test
    void register_newUser_returnsSuccessMessage() {
        // Given
        String run = "22222222-2";
        String clave = "1234";
        AuthRequest request = buildRequest(run, clave);
        when(usuarioService.register(run, clave)).thenReturn("¡Usuario creado exitosamente!");

        // When
        MessageResponse response = authController.register(request);

        // Then
        assertNotNull(response);
        assertEquals("¡Usuario creado exitosamente!", response.getMessage());
        verify(usuarioService).register(run, clave);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    void register_existingUser_returnsAlreadyExistsMessage() {
        // Given
        String run = "33333333-3";
        String clave = "1234";
        AuthRequest request = buildRequest(run, clave);
        when(usuarioService.register(run, clave)).thenReturn("¡Usuario ya existe!");

        // When
        MessageResponse response = authController.register(request);

        // Then
        assertNotNull(response);
        assertEquals("¡Usuario ya existe!", response.getMessage());
        verify(usuarioService).register(run, clave);
        verifyNoMoreInteractions(usuarioService);
    }
}
