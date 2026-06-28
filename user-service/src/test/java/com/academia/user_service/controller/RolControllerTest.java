package com.academia.user_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.academia.user_service.model.Rol;
import com.academia.user_service.service.RolService;

/**
 * Pure Mockito unit tests for {@link RolController}.
 * No Spring context, no database, no MockMvc: the controller methods are
 * invoked directly and the resulting {@link ResponseEntity} is asserted.
 */
@ExtendWith(MockitoExtension.class)
class RolControllerTest {

    @Mock
    private RolService rolService;

    @InjectMocks
    private RolController rolController;

    private Rol buildRol() {
        Rol rol = new Rol();
        rol.setId_rol(1L);
        rol.setNombreRol("ADMIN");
        return rol;
    }

    @Test
    @DisplayName("listar: returns all roles from the service")
    void listar_returnsAllRoles() {
        // Given
        List<Rol> roles = Arrays.asList(buildRol(), buildRol());
        when(rolService.listarTodos()).thenReturn(roles);

        // When
        List<Rol> result = rolController.listar();

        // Then
        assertEquals(2, result.size());
        verify(rolService, times(1)).listarTodos();
    }

    @Test
    @DisplayName("obtenerPorId: returns 200 with the role when it exists")
    void obtenerPorId_found() {
        // Given
        Rol rol = buildRol();
        when(rolService.buscarPorId(1L)).thenReturn(rol);

        // When
        ResponseEntity<Rol> response = rolController.obtenerPorId(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(rol, response.getBody());
        verify(rolService, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("obtenerPorId: returns 404 when the role does not exist")
    void obtenerPorId_notFound() {
        // Given
        when(rolService.buscarPorId(99L)).thenReturn(null);

        // When
        ResponseEntity<Rol> response = rolController.obtenerPorId(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(rolService, times(1)).buscarPorId(99L);
    }

    @Test
    @DisplayName("crear: returns 200 with the saved role")
    void crear_savesRole() {
        // Given
        Rol toCreate = buildRol();
        Rol saved = buildRol();
        when(rolService.guardarRol(toCreate)).thenReturn(saved);

        // When
        ResponseEntity<Rol> response = rolController.crear(toCreate);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(saved, response.getBody());
        verify(rolService, times(1)).guardarRol(toCreate);
    }

    @Test
    @DisplayName("actualizar: returns 200 with the updated role when it exists")
    void actualizar_found() {
        // Given
        Rol details = buildRol();
        Rol updated = buildRol();
        when(rolService.actualizarRol(eq(1L), any(Rol.class))).thenReturn(updated);

        // When
        ResponseEntity<Rol> response = rolController.actualizar(1L, details);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());
        verify(rolService, times(1)).actualizarRol(1L, details);
    }

    @Test
    @DisplayName("actualizar: returns 404 when the role does not exist")
    void actualizar_notFound() {
        // Given
        Rol details = buildRol();
        when(rolService.actualizarRol(eq(99L), any(Rol.class))).thenReturn(null);

        // When
        ResponseEntity<Rol> response = rolController.actualizar(99L, details);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(rolService, times(1)).actualizarRol(99L, details);
    }

    @Test
    @DisplayName("eliminar: returns 204 No Content when the role exists")
    void eliminar_success() {
        // Given
        when(rolService.eliminarRol(1L)).thenReturn(true);

        // When
        ResponseEntity<Void> response = rolController.eliminar(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(rolService, times(1)).eliminarRol(1L);
    }

    @Test
    @DisplayName("eliminar: returns 404 when the role does not exist")
    void eliminar_notFound() {
        // Given
        when(rolService.eliminarRol(99L)).thenReturn(false);

        // When
        ResponseEntity<Void> response = rolController.eliminar(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(rolService, times(1)).eliminarRol(99L);
    }
}
