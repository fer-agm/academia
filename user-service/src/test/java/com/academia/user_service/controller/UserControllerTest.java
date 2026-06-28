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

import com.academia.user_service.model.User;
import com.academia.user_service.service.UserService;

/**
 * Pure Mockito unit tests for {@link UserController}.
 * No Spring context, no database, no MockMvc: the controller methods are
 * invoked directly and the resulting {@link ResponseEntity} is asserted.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User buildUser() {
        User user = new User();
        user.setId(1L);
        user.setRun("11111111-1");
        user.setNombre("Ana");
        user.setApellido("Perez");
        user.setUsuario("aperez");
        user.setClave("1234");
        user.setEmail("aperez@banca.me");
        return user;
    }

    @Test
    @DisplayName("listar: returns all users from the service")
    void listar_returnsAllUsers() {
        // Given
        List<User> users = Arrays.asList(buildUser(), buildUser());
        when(userService.listarTodo()).thenReturn(users);

        // When
        List<User> result = userController.listar();

        // Then
        assertEquals(2, result.size());
        verify(userService, times(1)).listarTodo();
    }

    @Test
    @DisplayName("buscar: returns 200 with the user when it exists")
    void buscar_found() {
        // Given
        User user = buildUser();
        when(userService.buscarPorId(1L)).thenReturn(user);

        // When
        ResponseEntity<User> response = userController.buscar(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(user, response.getBody());
        verify(userService, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("buscar: returns 404 when the user does not exist")
    void buscar_notFound() {
        // Given
        when(userService.buscarPorId(99L)).thenReturn(null);

        // When
        ResponseEntity<User> response = userController.buscar(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).buscarPorId(99L);
    }

    @Test
    @DisplayName("crear: returns 200 with the saved user")
    void crear_savesUser() {
        // Given
        User toCreate = buildUser();
        User saved = buildUser();
        when(userService.guardarUsuario(toCreate)).thenReturn(saved);

        // When
        ResponseEntity<User> response = userController.crear(toCreate);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(saved, response.getBody());
        verify(userService, times(1)).guardarUsuario(toCreate);
    }

    @Test
    @DisplayName("actualizar: returns 200 with the updated user when it exists")
    void actualizar_found() {
        // Given
        User details = buildUser();
        User updated = buildUser();
        when(userService.actualizarUsuario(eq(1L), any(User.class))).thenReturn(updated);

        // When
        ResponseEntity<User> response = userController.actualizar(1L, details);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());
        verify(userService, times(1)).actualizarUsuario(1L, details);
    }

    @Test
    @DisplayName("actualizar: returns 404 when the user does not exist")
    void actualizar_notFound() {
        // Given
        User details = buildUser();
        when(userService.actualizarUsuario(eq(99L), any(User.class))).thenReturn(null);

        // When
        ResponseEntity<User> response = userController.actualizar(99L, details);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).actualizarUsuario(99L, details);
    }

    @Test
    @DisplayName("eliminar: returns 200 with confirmation message when the user exists")
    void eliminar_success() {
        // Given
        when(userService.eliminarUsuario(1L)).thenReturn(true);

        // When
        ResponseEntity<String> response = userController.eliminar(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuario eliminado", response.getBody());
        verify(userService, times(1)).eliminarUsuario(1L);
    }

    @Test
    @DisplayName("eliminar: returns 404 when the user does not exist")
    void eliminar_notFound() {
        // Given
        when(userService.eliminarUsuario(99L)).thenReturn(false);

        // When
        ResponseEntity<String> response = userController.eliminar(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).eliminarUsuario(99L);
    }
}
