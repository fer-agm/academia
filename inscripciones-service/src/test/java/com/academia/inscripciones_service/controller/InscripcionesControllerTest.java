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
        // Required so WebMvcLinkBuilder can build links outside of a real request
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
        // Given: a baseline Inscripciones used across tests
        inscripcion = new Inscripciones(1L, "11111111-1", 100L, LocalDateTime.now(), "ACTIVO");
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("listar devuelve la coleccion de inscripciones con links")
    void listar_devuelveLista() {
        // Given
        Inscripciones otra = new Inscripciones(2L, "22222222-2", 200L, LocalDateTime.now(), "ACTIVO");
        List<Inscripciones> esperadas = Arrays.asList(inscripcion, otra);
        when(inscripcionesService.listarTodas()).thenReturn(esperadas);

        // When
        CollectionModel<EntityModel<Inscripciones>> resultado = inscripcionesController.listar();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.getContent().size());
        assertTrue(resultado.getLink("self").isPresent());
        resultado.getContent().forEach(em -> {
            assertTrue(em.getLink("self").isPresent());
            assertTrue(em.getLink("listar").isPresent());
        });
        verify(inscripcionesService).listarTodas();
    }

    @Test
    @DisplayName("obtenerPorId devuelve 200 con la inscripcion y links cuando existe")
    void obtenerPorId_encontrado() {
        // Given
        when(inscripcionesService.buscarPorId(1L)).thenReturn(inscripcion);

        // When
        ResponseEntity<EntityModel<Inscripciones>> respuesta = inscripcionesController.obtenerPorId(1L);

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertSame(inscripcion, respuesta.getBody().getContent());
        assertTrue(respuesta.getBody().getLink("self").isPresent());
        assertTrue(respuesta.getBody().getLink("listar").isPresent());
        verify(inscripcionesService).buscarPorId(1L);
    }

    @Test
    @DisplayName("obtenerPorId devuelve 404 cuando no existe")
    void obtenerPorId_noEncontrado() {
        // Given
        when(inscripcionesService.buscarPorId(99L)).thenReturn(null);

        // When
        ResponseEntity<EntityModel<Inscripciones>> respuesta = inscripcionesController.obtenerPorId(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(inscripcionesService).buscarPorId(99L);
    }

    @Test
    @DisplayName("obtenerPorEstudiante devuelve 200 con la coleccion y links")
    void obtenerPorEstudiante_devuelveLista() {
        // Given
        List<Inscripciones> esperadas = Arrays.asList(inscripcion);
        when(inscripcionesService.listarPorEstudiante("11111111-1")).thenReturn(esperadas);

        // When
        ResponseEntity<CollectionModel<EntityModel<Inscripciones>>> respuesta =
                inscripcionesController.obtenerPorEstudiante("11111111-1");

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(1, respuesta.getBody().getContent().size());
        assertTrue(respuesta.getBody().getLink("self").isPresent());
        assertTrue(respuesta.getBody().getLink("listar").isPresent());
        verify(inscripcionesService).listarPorEstudiante("11111111-1");
    }

    @Test
    @DisplayName("obtenerPorEstudiante devuelve 200 con coleccion vacia cuando no hay inscripciones")
    void obtenerPorEstudiante_listaVacia() {
        // Given
        when(inscripcionesService.listarPorEstudiante("00000000-0")).thenReturn(List.of());

        // When
        ResponseEntity<CollectionModel<EntityModel<Inscripciones>>> respuesta =
                inscripcionesController.obtenerPorEstudiante("00000000-0");

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertTrue(respuesta.getBody().getContent().isEmpty());
        assertTrue(respuesta.getBody().getLink("self").isPresent());
        verify(inscripcionesService).listarPorEstudiante("00000000-0");
    }

    @Test
    @DisplayName("inscribir devuelve 200 con la inscripcion creada y link self")
    void inscribir_ok() {
        // Given
        when(inscripcionesService.crearInscripcion(inscripcion, "Bearer token")).thenReturn(inscripcion);

        // When
        ResponseEntity<EntityModel<Inscripciones>> respuesta = inscripcionesController.inscribir(inscripcion, "Bearer token");

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertSame(inscripcion, respuesta.getBody().getContent());
        assertTrue(respuesta.getBody().getLink("self").isPresent());
        verify(inscripcionesService).crearInscripcion(inscripcion, "Bearer token");
    }

    @Test
    @DisplayName("actualizar devuelve 200 con la inscripcion actualizada y link self cuando existe")
    void actualizar_encontrado() {
        // Given
        Inscripciones datos = new Inscripciones(null, null, 200L, null, "FINALIZADO");
        when(inscripcionesService.actualizar(1L, datos)).thenReturn(inscripcion);

        // When
        ResponseEntity<EntityModel<Inscripciones>> respuesta = inscripcionesController.actualizar(1L, datos);

        // Then
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertSame(inscripcion, respuesta.getBody().getContent());
        assertTrue(respuesta.getBody().getLink("self").isPresent());
        verify(inscripcionesService).actualizar(1L, datos);
    }

    @Test
    @DisplayName("actualizar devuelve 404 cuando la inscripcion no existe")
    void actualizar_noEncontrado() {
        // Given
        Inscripciones datos = new Inscripciones(null, null, 200L, null, "FINALIZADO");
        when(inscripcionesService.actualizar(99L, datos)).thenReturn(null);

        // When
        ResponseEntity<EntityModel<Inscripciones>> respuesta = inscripcionesController.actualizar(99L, datos);

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
