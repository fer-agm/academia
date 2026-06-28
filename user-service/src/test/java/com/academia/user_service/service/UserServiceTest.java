package com.academia.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.academia.user_service.model.User;
import com.academia.user_service.repository.RolRepository;
import com.academia.user_service.repository.UserRepository;

/**
 * Pure Mockito unit tests for {@link UserService}.
 * No Spring context, no database.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private UserService userService;

    private User buildUser() {
        User user = new User();
        user.setId(1L);
        user.setRun("11111111-1");
        user.setNombre("Ana");
        user.setApellido("Perez");
        user.setUsuario("aperez");
        user.setClave("secret");
        user.setEmail("aperez@banca.me");
        user.setIdRol(1L);
        return user;
    }

    @Test
    @DisplayName("guardarUsuario: persists the user")
    void guardarUsuario_savesUser() {
        // Given
        User user = buildUser();
        user.setFecha_Registro(new java.sql.Date(System.currentTimeMillis()));
        when(rolRepository.existsById(any())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);

        // When
        User result = userService.guardarUsuario(user);

        // Then
        assertNotNull(result);
        assertSame(user, result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("guardarUsuario: sets fecha_Registro when it is null")
    void guardarUsuario_setsFechaRegistroWhenNull() {
        // Given
        User user = buildUser();
        user.setFecha_Registro(null);
        when(rolRepository.existsById(any())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.guardarUsuario(user);

        // Then
        assertNotNull(result.getFecha_Registro());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("guardarUsuario: keeps the existing fecha_Registro when already set")
    void guardarUsuario_keepsExistingFechaRegistro() {
        // Given
        User user = buildUser();
        java.sql.Date original = new java.sql.Date(0L);
        user.setFecha_Registro(original);
        when(rolRepository.existsById(any())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.guardarUsuario(user);

        // Then
        assertSame(original, result.getFecha_Registro());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("guardarUsuario: throws when the idRol does not exist")
    void guardarUsuario_throwsWhenRolDoesNotExist() {
        // Given
        User user = buildUser();
        user.setIdRol(999L);
        when(rolRepository.existsById(any())).thenReturn(false);

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.guardarUsuario(user));
        assertEquals("El rol con id 999 no existe", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("guardarUsuario: throws when the run already exists")
    void guardarUsuario_throwsWhenRunExists() {
        // Given
        User user = buildUser();
        when(userRepository.findByRun("11111111-1")).thenReturn(Optional.of(buildUser()));

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.guardarUsuario(user));
        assertEquals("Ya existe un usuario con el run 11111111-1", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("guardarUsuario: throws when the email already exists")
    void guardarUsuario_throwsWhenEmailExists() {
        // Given
        User user = buildUser();
        when(userRepository.findByRun("11111111-1")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("aperez@banca.me")).thenReturn(Optional.of(buildUser()));

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.guardarUsuario(user));
        assertEquals("Ya existe un usuario con el email aperez@banca.me", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("listarTodo: returns all users from the repository")
    void listarTodo_returnsAllUsers() {
        // Given
        List<User> users = Arrays.asList(buildUser(), buildUser());
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.listarTodo();

        // Then
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId: returns the user when it exists")
    void buscarPorId_found() {
        // Given
        User user = buildUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        User result = userService.buscarPorId(1L);

        // Then
        assertNotNull(result);
        assertSame(user, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId: returns null when the user does not exist")
    void buscarPorId_notFound() {
        // Given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        User result = userService.buscarPorId(99L);

        // Then
        assertNull(result);
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("buscarPorRun: returns the user when the RUN exists")
    void buscarPorRun_found() {
        // Given
        User user = buildUser();
        when(userRepository.findByRun("11111111-1")).thenReturn(Optional.of(user));

        // When
        User result = userService.buscarPorRun("11111111-1");

        // Then
        assertNotNull(result);
        assertSame(user, result);
        verify(userRepository, times(1)).findByRun("11111111-1");
    }

    @Test
    @DisplayName("buscarPorRun: returns null when the RUN does not exist")
    void buscarPorRun_notFound() {
        // Given
        when(userRepository.findByRun("00000000-0")).thenReturn(Optional.empty());

        // When
        User result = userService.buscarPorRun("00000000-0");

        // Then
        assertNull(result);
        verify(userRepository, times(1)).findByRun("00000000-0");
    }

    @Test
    @DisplayName("actualizarUsuario: updates fields and saves when the user exists")
    void actualizarUsuario_found() {
        // Given
        User existing = buildUser();
        User nuevosDatos = new User();
        nuevosDatos.setNombre("Beatriz");
        nuevosDatos.setApellido("Soto");
        nuevosDatos.setUsuario("bsoto");
        nuevosDatos.setClave("newpass");
        nuevosDatos.setEmail("bsoto@banca.me");
        nuevosDatos.setIdRol(1L);
        when(rolRepository.existsById(any())).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        // When
        User result = userService.actualizarUsuario(1L, nuevosDatos);

        // Then
        assertNotNull(result);
        assertEquals("Beatriz", result.getNombre());
        assertEquals("Soto", result.getApellido());
        assertEquals("bsoto", result.getUsuario());
        assertEquals("newpass", result.getClave());
        assertEquals("bsoto@banca.me", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("actualizarUsuario: throws when the new email belongs to another user")
    void actualizarUsuario_throwsWhenEmailExists() {
        // Given: the user exists (email aperez@banca.me) and we try to change it to one already taken
        User existing = buildUser();
        User nuevosDatos = new User();
        nuevosDatos.setNombre("Beatriz");
        nuevosDatos.setApellido("Soto");
        nuevosDatos.setUsuario("bsoto");
        nuevosDatos.setClave("newpass");
        nuevosDatos.setEmail("otro@banca.me");
        nuevosDatos.setIdRol(1L);
        when(rolRepository.existsById(any())).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("otro@banca.me")).thenReturn(Optional.of(buildUser()));

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.actualizarUsuario(1L, nuevosDatos));
        assertEquals("Ya existe un usuario con el email otro@banca.me", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("actualizarUsuario: returns null and does not save when the user does not exist")
    void actualizarUsuario_notFound() {
        // Given
        User nuevosDatos = buildUser();
        when(rolRepository.existsById(any())).thenReturn(true);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        User result = userService.actualizarUsuario(99L, nuevosDatos);

        // Then
        assertNull(result);
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("eliminarUsuario: returns true and deletes when the user exists")
    void eliminarUsuario_exists() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = userService.eliminarUsuario(1L);

        // Then
        assertTrue(result);
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarUsuario: returns false and does not delete when the user does not exist")
    void eliminarUsuario_notExists() {
        // Given
        when(userRepository.existsById(99L)).thenReturn(false);

        // When
        boolean result = userService.eliminarUsuario(99L);

        // Then
        assertFalse(result);
        verify(userRepository, times(1)).existsById(99L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}
