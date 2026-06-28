package com.academia.inscripciones_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.academia.inscripciones_service.model.Inscripciones;
import com.academia.inscripciones_service.service.InscripcionesService;

@ExtendWith(MockitoExtension.class)
class InscripcionesControllerTest {

    @Mock
    private InscripcionesService inscripcionesService;

    @InjectMocks
    private InscripcionesController inscripcionesController;

    private Inscripciones inscripcion;

    @BeforeEach
    void setUp() {
        // Given: a baseline Inscripciones used across tests
        inscripcion = new Inscripciones(1L, "11111111-1", 100L, LocalDateTime.now(), "ACTIVO");
    }

    @Test
    @DisplayName("listar devuelve la lista de inscripciones del servicio")
    void listar_devuelveLista() {
        // Given
        Inscripciones otra = new Inscripciones(2L, "22222222-2", 200L, LocalDateTime.now(), "ACTIVO");
        List<Inscripciones> esperadas = Arrays.asList(inscripcion, otra);
        when(inscripcionesService.listarTodas()).thenReturn(esperadas);

        // When
        List<Inscripciones> resultado = inscripcionesController.listar();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(esperadas, resultado);
        verify(inscripcionesService).listarTodas();
    }

    @Test
    @DisplayName("obtenerPorId devuelve 200 con la inscripcion cuando existe")
    void obtenerPorId_encontrado() {
        // Given
        when(inscripcionesService.buscarPorId(1L)).thenReturn(inscripcion);

        // When
        ResponseEntity<Inscripciones> respuesta = inscripcionesController.obtenerPorId(1L);

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertSame(inscripcion, respuesta.getBody());
        verify(inscripcionesService).buscarPorId(1L);
    }

    @Test
    @DisplayName("obtenerPorId devuelve 404 cuando no existe")
    void obtenerPorId_noEncontrado() {
        // Given
        when(inscripcionesService.buscarPorId(99L)).thenReturn(null);

        // When
        ResponseEntity<Inscripciones> respuesta = inscripcionesController.obtenerPorId(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(inscripcionesService).buscarPorId(99L);
    }

    @Test
    @DisplayName("obtenerPorEstudiante devuelve 200 con las inscripciones del estudiante")
    void obtenerPorEstudiante_devuelveLista() {
        // Given
        List<Inscripciones> esperadas = Arrays.asList(inscripcion);
        when(inscripcionesService.listarPorEstudiante("11111111-1")).thenReturn(esperadas);

        // When
        ResponseEntity<List<Inscripciones>> respuesta =
                inscripcionesController.obtenerPorEstudiante("11111111-1");

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(esperadas, respuesta.getBody());
        verify(inscripcionesService).listarPorEstudiante("11111111-1");
    }

    @Test
    @DisplayName("obtenerPorEstudiante devuelve 200 con lista vacia cuando no hay inscripciones")
    void obtenerPorEstudiante_listaVacia() {
        // Given
        when(inscripcionesService.listarPorEstudiante("00000000-0")).thenReturn(List.of());

        // When
        ResponseEntity<List<Inscripciones>> respuesta =
                inscripcionesController.obtenerPorEstudiante("00000000-0");

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertTrue(respuesta.getBody().isEmpty());
        verify(inscripcionesService).listarPorEstudiante("00000000-0");
    }

    @Test
    @DisplayName("inscribir devuelve 200 con la inscripcion creada")
    void inscribir_ok() {
        // Given
        when(inscripcionesService.crearInscripcion(inscripcion)).thenReturn(inscripcion);

        // When
        ResponseEntity<Inscripciones> respuesta = inscripcionesController.inscribir(inscripcion);

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertSame(inscripcion, respuesta.getBody());
        verify(inscripcionesService).crearInscripcion(inscripcion);
    }

    @Test
    @DisplayName("actualizar devuelve 200 con la inscripcion actualizada cuando existe")
    void actualizar_encontrado() {
        // Given
        Inscripciones datos = new Inscripciones(null, null, 200L, null, "FINALIZADO");
        when(inscripcionesService.actualizar(1L, datos)).thenReturn(inscripcion);

        // When
        ResponseEntity<Inscripciones> respuesta = inscripcionesController.actualizar(1L, datos);

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertSame(inscripcion, respuesta.getBody());
        verify(inscripcionesService).actualizar(1L, datos);
    }

    @Test
    @DisplayName("actualizar devuelve 404 cuando la inscripcion no existe")
    void actualizar_noEncontrado() {
        // Given
        Inscripciones datos = new Inscripciones(null, null, 200L, null, "FINALIZADO");
        when(inscripcionesService.actualizar(99L, datos)).thenReturn(null);

        // When
        ResponseEntity<Inscripciones> respuesta = inscripcionesController.actualizar(99L, datos);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(inscripcionesService).actualizar(99L, datos);
    }

    @Test
    @DisplayName("eliminar devuelve 204 cuando la inscripcion existe")
    void eliminar_existente() {
        // Given
        when(inscripcionesService.eliminar(1L)).thenReturn(true);

        // When
        ResponseEntity<Void> respuesta = inscripcionesController.eliminar(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(inscripcionesService).eliminar(1L);
    }

    @Test
    @DisplayName("eliminar devuelve 404 cuando la inscripcion no existe")
    void eliminar_inexistente() {
        // Given
        when(inscripcionesService.eliminar(99L)).thenReturn(false);

        // When
        ResponseEntity<Void> respuesta = inscripcionesController.eliminar(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        verify(inscripcionesService).eliminar(99L);
    }
}
