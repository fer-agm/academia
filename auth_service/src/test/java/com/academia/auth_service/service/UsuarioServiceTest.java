package com.academia.auth_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @Mock
    private HashService hashService;

    @InjectMocks
    private UsuarioService usuarioService;

    // ---------- login ----------

    @Test
    void login_validCredentials_returnsToken() {
        // Given
        String run = "11111111-1";
        String clave = "1234";
        String hashedClave = "hashed-1234";

        Usuario usuario = new Usuario();
        usuario.setRun(run);
        usuario.setClave(hashedClave);

        when(usuarioRepository.findByRun(run)).thenReturn(Optional.of(usuario));
        when(hashService.sha1(clave)).thenReturn(hashedClave);
        when(jwtService.generateToken(run)).thenReturn("jwt-token");

        // When
        String result = usuarioService.login(run, clave);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token", result);
        verify(usuarioRepository).findByRun(run);
        verify(hashService).sha1(clave);
        verify(jwtService).generateToken(run);
    }

    @Test
    void login_userNotFound_returnsNull() {
        // Given
        String run = "00000000-0";
        String clave = "1234";
        when(usuarioRepository.findByRun(run)).thenReturn(Optional.empty());

        // When
        String result = usuarioService.login(run, clave);

        // Then
        assertNull(result);
        verify(usuarioRepository).findByRun(run);
        verify(hashService, never()).sha1(anyString());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void login_wrongPassword_returnsNull() {
        // Given
        String run = "11111111-1";
        String clave = "wrong";

        Usuario usuario = new Usuario();
        usuario.setRun(run);
        usuario.setClave("hash-of-correct-password");

        when(usuarioRepository.findByRun(run)).thenReturn(Optional.of(usuario));
        when(hashService.sha1(clave)).thenReturn("hash-of-wrong-password");

        // When
        String result = usuarioService.login(run, clave);

        // Then
        assertNull(result);
        verify(hashService).sha1(clave);
        verify(jwtService, never()).generateToken(anyString());
    }

    // ---------- register ----------

    @Test
    void register_newUser_savesAndReturnsSuccessMessage() {
        // Given
        String run = "22222222-2";
        String clave = "1234";
        String hashedClave = "hashed-1234";

        when(usuarioRepository.findByRun(run)).thenReturn(Optional.empty());
        when(hashService.sha1(clave)).thenReturn(hashedClave);

        // When
        String result = usuarioService.register(run, clave);

        // Then
        assertEquals("¡Usuario creado exitosamente!", result);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario saved = captor.getValue();
        assertEquals(run, saved.getRun());
        assertEquals(hashedClave, saved.getClave());
        assertEquals("", saved.getNombre());
        assertEquals("", saved.getApellido());
        assertEquals(run + "@academia.cl", saved.getEmail());
        verify(hashService).sha1(clave);
    }

    @Test
    void register_existingUser_returnsAlreadyExistsAndDoesNotSave() {
        // Given
        String run = "33333333-3";
        String clave = "1234";

        Usuario existing = new Usuario();
        existing.setRun(run);
        when(usuarioRepository.findByRun(run)).thenReturn(Optional.of(existing));

        // When
        String result = usuarioService.register(run, clave);

        // Then
        assertEquals("¡Usuario ya existe!", result);
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(hashService, never()).sha1(anyString());
    }

    @Test
    void register_newUser_invokesFindByRunExactlyOnce() {
        // Given
        String run = "44444444-4";
        String clave = "abcd";
        when(usuarioRepository.findByRun(run)).thenReturn(Optional.empty());
        when(hashService.sha1(clave)).thenReturn("hash-abcd");

        // When
        usuarioService.register(run, clave);

        // Then
        verify(usuarioRepository, times(1)).findByRun(run);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
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
