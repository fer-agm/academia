package com.academia.evaluaciones_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.academia.evaluaciones_service.model.Evaluaciones;
import com.academia.evaluaciones_service.service.EvaluacionesService;

@ExtendWith(MockitoExtension.class)
class EvaluacionesControllerTest {

    @Mock
    private EvaluacionesService evaluacionesService;

    @InjectMocks
    private EvaluacionesController evaluacionesController;

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    private Evaluaciones sampleEvaluacion() {
        return new Evaluaciones(1L, 5L, 40, 100);
    }

    @Test
    void getAllEvaluaciones_returnsOkWithList() {
        // Given
        List<Evaluaciones> evaluaciones = List.of(sampleEvaluacion());
        when(evaluacionesService.getAllEvaluaciones()).thenReturn(evaluaciones);

        // When
        CollectionModel<EntityModel<Evaluaciones>> response = evaluacionesController.getAllEvaluaciones();

        // Then
        assertEquals(1, response.getContent().size());
        EntityModel<Evaluaciones> item = response.getContent().iterator().next();
        assertSame(evaluaciones.get(0), item.getContent());
        assertTrue(item.getLink(IanaLinkRelations.SELF).isPresent());
        assertTrue(item.getLink("listar").isPresent());
        assertTrue(response.getLink(IanaLinkRelations.SELF).isPresent());
        verify(evaluacionesService, times(1)).getAllEvaluaciones();
    }

    @Test
    void getEvaluacionById_whenFound_returnsOk() {
        // Given
        Evaluaciones evaluacion = sampleEvaluacion();
        when(evaluacionesService.getEvaluacionById(1L)).thenReturn(Optional.of(evaluacion));

        // When
        ResponseEntity<EntityModel<Evaluaciones>> response = evaluacionesController.getEvaluacionById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(evaluacion, response.getBody().getContent());
        assertTrue(response.getBody().getLink(IanaLinkRelations.SELF).isPresent());
        assertTrue(response.getBody().getLink("listar").isPresent());
        verify(evaluacionesService, times(1)).getEvaluacionById(1L);
    }

    @Test
    void getEvaluacionById_whenNotFound_returnsNotFound() {
        // Given
        when(evaluacionesService.getEvaluacionById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<EntityModel<Evaluaciones>> response = evaluacionesController.getEvaluacionById(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(evaluacionesService, times(1)).getEvaluacionById(99L);
    }

    @Test
    void crearEvaluacion_returnsOkWithSavedEntity() {
        // Given
        Evaluaciones toSave = sampleEvaluacion();
        Evaluaciones saved = sampleEvaluacion();
        when(evaluacionesService.guardar(toSave)).thenReturn(saved);

        // When
        ResponseEntity<EntityModel<Evaluaciones>> response = evaluacionesController.crearEvaluacion(toSave);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(saved, response.getBody().getContent());
        assertTrue(response.getBody().getLink(IanaLinkRelations.SELF).isPresent());
        verify(evaluacionesService, times(1)).guardar(toSave);
    }

    @Test
    void actualizar_whenFound_returnsOkAndSetsId() {
        // Given
        Evaluaciones existing = sampleEvaluacion();
        Evaluaciones incoming = new Evaluaciones(null, 7L, 50, 90);
        when(evaluacionesService.getEvaluacionById(1L)).thenReturn(Optional.of(existing));
        when(evaluacionesService.guardar(any(Evaluaciones.class))).thenReturn(incoming);

        // When
        ResponseEntity<EntityModel<Evaluaciones>> response = evaluacionesController.actualizar(1L, incoming);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(incoming, response.getBody().getContent());
        assertTrue(response.getBody().getLink(IanaLinkRelations.SELF).isPresent());
        assertEquals(1L, incoming.getIdEvaluacion());
        verify(evaluacionesService, times(1)).getEvaluacionById(1L);
        verify(evaluacionesService, times(1)).guardar(incoming);
    }

    @Test
    void actualizar_whenNotFound_returnsNotFound() {
        // Given
        Evaluaciones incoming = new Evaluaciones(null, 7L, 50, 90);
        when(evaluacionesService.getEvaluacionById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<EntityModel<Evaluaciones>> response = evaluacionesController.actualizar(99L, incoming);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(evaluacionesService, times(1)).getEvaluacionById(99L);
        verify(evaluacionesService, never()).guardar(any(Evaluaciones.class));
    }

    @Test
    void borrar_whenFound_returnsNoContent() {
        // Given
        when(evaluacionesService.getEvaluacionById(1L)).thenReturn(Optional.of(sampleEvaluacion()));

        // When
        ResponseEntity<Void> response = evaluacionesController.borrar(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(evaluacionesService, times(1)).getEvaluacionById(1L);
        verify(evaluacionesService, times(1)).borrar(1L);
    }

    @Test
    void borrar_whenNotFound_returnsNotFound() {
        // Given
        when(evaluacionesService.getEvaluacionById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Void> response = evaluacionesController.borrar(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(evaluacionesService, times(1)).getEvaluacionById(99L);
        verify(evaluacionesService, never()).borrar(any());
    }
}
