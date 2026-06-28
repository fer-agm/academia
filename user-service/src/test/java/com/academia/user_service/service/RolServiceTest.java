package com.academia.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.academia.user_service.model.Rol;
import com.academia.user_service.repository.RolRepository;

/**
 * Pure Mockito unit tests for {@link RolService}.
 * No Spring context, no database.
 */
@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    private Rol buildRol() {
        Rol rol = new Rol();
        rol.setId_rol(1L);
        rol.setNombreRol("ADMIN");
        return rol;
    }

    @Test
    @DisplayName("guardarRol: persists the role and returns the saved instance")
    void guardarRol_savesRole() {
        // Given
        Rol rol = buildRol();
        when(rolRepository.save(rol)).thenReturn(rol);

        // When
        Rol result = rolService.guardarRol(rol);

        // Then
        assertNotNull(result);
        assertSame(rol, result);
        verify(rolRepository, times(1)).save(rol);
    }

    @Test
    @DisplayName("listarTodos: returns all roles from the repository")
    void listarTodos_returnsAllRoles() {
        // Given
        List<Rol> roles = Arrays.asList(buildRol(), buildRol());
        when(rolRepository.findAll()).thenReturn(roles);

        // When
        List<Rol> result = rolService.listarTodos();

        // Then
        assertEquals(2, result.size());
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId: returns the role when it exists")
    void buscarPorId_found() {
        // Given
        Rol rol = buildRol();
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        // When
        Rol result = rolService.buscarPorId(1L);

        // Then
        assertNotNull(result);
        assertSame(rol, result);
        verify(rolRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId: returns null when the role does not exist")
    void buscarPorId_notFound() {
        // Given
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Rol result = rolService.buscarPorId(99L);

        // Then
        assertNull(result);
        verify(rolRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("actualizarRol: updates the name and saves when the role exists")
    void actualizarRol_found() {
        // Given
        Rol existing = buildRol();
        Rol nuevosDatos = new Rol();
        nuevosDatos.setNombreRol("USER");
        when(rolRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(rolRepository.save(existing)).thenReturn(existing);

        // When
        Rol result = rolService.actualizarRol(1L, nuevosDatos);

        // Then
        assertNotNull(result);
        assertEquals("USER", result.getNombreRol());
        verify(rolRepository, times(1)).findById(1L);
        verify(rolRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("actualizarRol: returns null and does not save when the role does not exist")
    void actualizarRol_notFound() {
        // Given
        Rol nuevosDatos = buildRol();
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Rol result = rolService.actualizarRol(99L, nuevosDatos);

        // Then
        assertNull(result);
        verify(rolRepository, times(1)).findById(99L);
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    @DisplayName("eliminarRol: returns true and deletes when the role exists")
    void eliminarRol_exists() {
        // Given
        when(rolRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = rolService.eliminarRol(1L);

        // Then
        assertTrue(result);
        verify(rolRepository, times(1)).existsById(1L);
        verify(rolRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarRol: returns false and does not delete when the role does not exist")
    void eliminarRol_notExists() {
        // Given
        when(rolRepository.existsById(99L)).thenReturn(false);

        // When
        boolean result = rolService.eliminarRol(99L);

        // Then
        assertFalse(result);
        verify(rolRepository, times(1)).existsById(99L);
        verify(rolRepository, never()).deleteById(anyLong());
    }
}
