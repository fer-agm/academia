package com.academia.pago_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.academia.pago_service.model.Transaccion;
import com.academia.pago_service.service.TransaccionService;

/**
 * Pure Mockito unit tests for {@link TransaccionController}.
 * No Spring context, no database, no MockMvc, no @WebMvcTest.
 */
@ExtendWith(MockitoExtension.class)
class TransaccionControllerTest {

    @Mock
    private TransaccionService transaccionService;

    @InjectMocks
    private TransaccionController transaccionController;

    @BeforeEach
    void setUpRequestContext() {
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDownRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    private Transaccion nuevaTransaccion(Long id) {
        Transaccion t = new Transaccion();
        t.setId_transaccion(id);
        t.setMetodo("TARJETA_CREDITO");
        t.setFecha(LocalDateTime.of(2026, 1, 1, 12, 0));
        return t;
    }

    // ---------------------------------------------------------------------
    // listar()
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("listar: devuelve la lista del servicio")
    void listar_ok() {
        // Given
        List<Transaccion> transacciones = Arrays.asList(nuevaTransaccion(1L), nuevaTransaccion(2L));
        when(transaccionService.listarTodas()).thenReturn(transacciones);

        // When
        CollectionModel<EntityModel<Transaccion>> result = transaccionController.listar();

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getLink("self").isPresent());
        result.getContent().forEach(em -> assertTrue(em.getLink("self").isPresent()));
        verify(transaccionService).listarTodas();
    }

    // ---------------------------------------------------------------------
    // buscarPorId(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("buscarPorId: existe -> 200 con la transaccion")
    void buscarPorId_existe() {
        // Given
        Transaccion t = nuevaTransaccion(1L);
        when(transaccionService.buscarPorId(1L)).thenReturn(t);

        // When
        ResponseEntity<EntityModel<Transaccion>> response = transaccionController.buscarPorId(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(t, response.getBody().getContent());
        assertTrue(response.getBody().getLink("self").isPresent());
        assertTrue(response.getBody().getLink("listar").isPresent());
        verify(transaccionService).buscarPorId(1L);
    }

    @Test
    @DisplayName("buscarPorId: no existe (null) -> 404 sin cuerpo")
    void buscarPorId_noExiste() {
        // Given
        when(transaccionService.buscarPorId(99L)).thenReturn(null);

        // When
        ResponseEntity<EntityModel<Transaccion>> response = transaccionController.buscarPorId(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(transaccionService).buscarPorId(99L);
    }

    // ---------------------------------------------------------------------
    // generar(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("generar: registra y devuelve 200 con la transaccion guardada")
    void generar_ok() {
        // Given
        Transaccion entrada = nuevaTransaccion(null);
        Transaccion guardada = nuevaTransaccion(1L);
        when(transaccionService.registrarTransaccion(entrada)).thenReturn(guardada);

        // When
        ResponseEntity<EntityModel<Transaccion>> response = transaccionController.generar(entrada);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(guardada, response.getBody().getContent());
        assertTrue(response.getBody().getLink("self").isPresent());
        verify(transaccionService).registrarTransaccion(entrada);
    }

    // ---------------------------------------------------------------------
    // actualizar(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("actualizar: existe -> 200 con la transaccion actualizada")
    void actualizar_existe() {
        // Given
        Transaccion entrada = nuevaTransaccion(1L);
        Transaccion actualizada = nuevaTransaccion(1L);
        when(transaccionService.actualizarTransaccion(eq(1L), any(Transaccion.class)))
                .thenReturn(actualizada);

        // When
        ResponseEntity<EntityModel<Transaccion>> response = transaccionController.actualizar(1L, entrada);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(actualizada, response.getBody().getContent());
        assertTrue(response.getBody().getLink("self").isPresent());
        verify(transaccionService).actualizarTransaccion(eq(1L), any(Transaccion.class));
    }

    @Test
    @DisplayName("actualizar: no existe (null) -> 404 sin cuerpo")
    void actualizar_noExiste() {
        // Given
        Transaccion entrada = nuevaTransaccion(99L);
        when(transaccionService.actualizarTransaccion(eq(99L), any(Transaccion.class)))
                .thenReturn(null);

        // When
        ResponseEntity<EntityModel<Transaccion>> response = transaccionController.actualizar(99L, entrada);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(transaccionService).actualizarTransaccion(eq(99L), any(Transaccion.class));
    }

    // ---------------------------------------------------------------------
    // eliminar(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("eliminar: existe (true) -> 204 No Content")
    void eliminar_existe() {
        // Given
        when(transaccionService.eliminar(1L)).thenReturn(true);

        // When
        ResponseEntity<Void> response = transaccionController.eliminar(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transaccionService).eliminar(1L);
    }

    @Test
    @DisplayName("eliminar: no existe (false) -> 404 Not Found")
    void eliminar_noExiste() {
        // Given
        when(transaccionService.eliminar(99L)).thenReturn(false);

        // When
        ResponseEntity<Void> response = transaccionController.eliminar(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(transaccionService).eliminar(99L);
    }
}
