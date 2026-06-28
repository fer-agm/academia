package com.academia.inscripciones_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.academia.inscripciones_service.model.Cupos;
import com.academia.inscripciones_service.service.CuposService;

@ExtendWith(MockitoExtension.class)
class CuposControllerTest {

    @Mock
    private CuposService cuposService;

    @InjectMocks
    private CuposController cuposController;

    private Cupos cupo;

    @BeforeEach
    void setUp() {
        // Given: a baseline Cupos used across tests
        cupo = new Cupos(1L, 100L, 30, 10);
    }

    @Test
    @DisplayName("listar devuelve la lista de cupos del servicio")
    void listar_devuelveLista() {
        // Given
        Cupos otro = new Cupos(2L, 200L, 40, 5);
        List<Cupos> esperados = Arrays.asList(cupo, otro);
        when(cuposService.listarTodos()).thenReturn(esperados);

        // When
        List<Cupos> resultado = cuposController.listar();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(esperados, resultado);
        verify(cuposService).listarTodos();
    }

    @Test
    @DisplayName("obtenerPorId devuelve 200 con el cupo cuando existe")
    void obtenerPorId_encontrado() {
        // Given
        when(cuposService.buscarPorId(1L)).thenReturn(cupo);

        // When
        ResponseEntity<Cupos> respuesta = cuposController.obtenerPorId(1L);

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertSame(cupo, respuesta.getBody());
        verify(cuposService).buscarPorId(1L);
    }

    @Test
    @DisplayName("obtenerPorId devuelve 404 cuando no existe")
    void obtenerPorId_noEncontrado() {
        // Given
        when(cuposService.buscarPorId(99L)).thenReturn(null);

        // When
        ResponseEntity<Cupos> respuesta = cuposController.obtenerPorId(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(cuposService).buscarPorId(99L);
    }

    @Test
    @DisplayName("consultarPorCurso devuelve 200 con el cupo cuando existe")
    void consultarPorCurso_encontrado() {
        // Given
        when(cuposService.obtenerPorCurso(100L)).thenReturn(cupo);

        // When
        ResponseEntity<Cupos> respuesta = cuposController.consultarPorCurso(100L);

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertSame(cupo, respuesta.getBody());
        verify(cuposService).obtenerPorCurso(100L);
    }

    @Test
    @DisplayName("consultarPorCurso devuelve 404 cuando el curso no tiene cupo")
    void consultarPorCurso_noEncontrado() {
        // Given
        when(cuposService.obtenerPorCurso(500L)).thenReturn(null);

        // When
        ResponseEntity<Cupos> respuesta = cuposController.consultarPorCurso(500L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(cuposService).obtenerPorCurso(500L);
    }

    @Test
    @DisplayName("crear devuelve 200 con el cupo guardado")
    void crear_ok() {
        // Given
        when(cuposService.guardarCupo(cupo)).thenReturn(cupo);

        // When
        ResponseEntity<Cupos> respuesta = cuposController.crear(cupo);

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertSame(cupo, respuesta.getBody());
        verify(cuposService).guardarCupo(cupo);
    }

    @Test
    @DisplayName("actualizar devuelve 200 con el cupo actualizado cuando existe")
    void actualizar_encontrado() {
        // Given
        Cupos datos = new Cupos(null, null, 50, 20);
        when(cuposService.actualizar(1L, datos)).thenReturn(cupo);

        // When
        ResponseEntity<Cupos> respuesta = cuposController.actualizar(1L, datos);

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertSame(cupo, respuesta.getBody());
        verify(cuposService).actualizar(1L, datos);
    }

    @Test
    @DisplayName("actualizar devuelve 404 cuando el cupo no existe")
    void actualizar_noEncontrado() {
        // Given
        Cupos datos = new Cupos(null, null, 50, 20);
        when(cuposService.actualizar(99L, datos)).thenReturn(null);

        // When
        ResponseEntity<Cupos> respuesta = cuposController.actualizar(99L, datos);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(cuposService).actualizar(99L, datos);
    }

    @Test
    @DisplayName("eliminar devuelve 204 cuando el cupo existe")
    void eliminar_existente() {
        // Given
        when(cuposService.eliminar(1L)).thenReturn(true);

        // When
        ResponseEntity<Void> respuesta = cuposController.eliminar(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        verify(cuposService).eliminar(1L);
    }

    @Test
    @DisplayName("eliminar devuelve 404 cuando el cupo no existe")
    void eliminar_inexistente() {
        // Given
        when(cuposService.eliminar(99L)).thenReturn(false);

        // When
        ResponseEntity<Void> respuesta = cuposController.eliminar(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        verify(cuposService).eliminar(99L);
    }

    @Test
    @DisplayName("descontar devuelve 200 con mensaje cuando hay cupos disponibles")
    void descontar_conDisponibles() {
        // Given
        when(cuposService.reducirCupo(100L)).thenReturn(true);

        // When
        ResponseEntity<String> respuesta = cuposController.descontar(100L);

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Cupo descontado exitosamente", respuesta.getBody());
        verify(cuposService).reducirCupo(100L);
    }

    @Test
    @DisplayName("descontar devuelve 400 cuando no hay cupos disponibles")
    void descontar_sinDisponibles() {
        // Given
        when(cuposService.reducirCupo(300L)).thenReturn(false);

        // When
        ResponseEntity<String> respuesta = cuposController.descontar(300L);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals("No hay cupos disponibles", respuesta.getBody());
        verify(cuposService).reducirCupo(300L);
    }
}
