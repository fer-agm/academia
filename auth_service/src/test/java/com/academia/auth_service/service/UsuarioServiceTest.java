package com.academia.auth_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.academia.auth_service.model.Usuario;
import com.academia.auth_service.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UsuarioService usuarioService;

    // ---------- login (valida run + clave en texto plano contra 'usuarios') ----------

    @Test
    void login_validCredentials_returnsToken() {
        // Given
        String run = "11111111-1";
        String clave = "1234";

        Usuario usuario = new Usuario();
        usuario.setRun(run);
        usuario.setClave(clave);

        when(usuarioRepository.findByRun(run)).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(run)).thenReturn("jwt-token");

        // When
        String result = usuarioService.login(run, clave);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token", result);
        verify(usuarioRepository).findByRun(run);
        verify(jwtService).generateToken(run);
    }

    @Test
    void login_userNotFound_returnsNull() {
        // Given
        String run = "00000000-0";
        when(usuarioRepository.findByRun(run)).thenReturn(Optional.empty());

        // When
        String result = usuarioService.login(run, "1234");

        // Then
        assertNull(result);
        verify(usuarioRepository).findByRun(run);
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void login_wrongPassword_returnsNull() {
        // Given
        String run = "11111111-1";

        Usuario usuario = new Usuario();
        usuario.setRun(run);
        usuario.setClave("correcta");

        when(usuarioRepository.findByRun(run)).thenReturn(Optional.of(usuario));

        // When
        String result = usuarioService.login(run, "incorrecta");

        // Then
        assertNull(result);
        verify(jwtService, never()).generateToken(anyString());
    }

    // ---------- existeUsuario ----------

    @Test
    void existeUsuario_whenPresent_returnsTrue() {
        // Given
        String run = "11111111-1";
        when(usuarioRepository.findByRun(run)).thenReturn(Optional.of(new Usuario()));

        // When
        boolean result = usuarioService.existeUsuario(run);

        // Then
        assertTrue(result);
        verify(usuarioRepository).findByRun(run);
    }

    @Test
    void existeUsuario_whenAbsent_returnsFalse() {
        // Given
        String run = "00000000-0";
        when(usuarioRepository.findByRun(run)).thenReturn(Optional.empty());

        // When
        boolean result = usuarioService.existeUsuario(run);

        // Then
        assertFalse(result);
        verify(usuarioRepository).findByRun(run);
    }
}
