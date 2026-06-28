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

import com.academia.evaluaciones_service.model.Preguntas;
import com.academia.evaluaciones_service.service.PreguntasService;

@ExtendWith(MockitoExtension.class)
class PreguntasControllerTest {

    @Mock
    private PreguntasService preguntasService;

    @InjectMocks
    private PreguntasController preguntasController;

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    private Preguntas samplePregunta() {
        return new Preguntas(1L, "Cual es la capital?", 5, 20L);
    }

    @Test
    void getAll_returnsOkWithList() {
        // Given
        List<Preguntas> preguntas = List.of(samplePregunta());
        when(preguntasService.getAll()).thenReturn(preguntas);

        // When
        CollectionModel<EntityModel<Preguntas>> response = preguntasController.getAll();

        // Then
        assertEquals(1, response.getContent().size());
        EntityModel<Preguntas> item = response.getContent().iterator().next();
        assertSame(preguntas.get(0), item.getContent());
        assertTrue(item.getLink(IanaLinkRelations.SELF).isPresent());
        assertTrue(item.getLink("listar").isPresent());
        assertTrue(response.getLink(IanaLinkRelations.SELF).isPresent());
        verify(preguntasService, times(1)).getAll();
    }

    @Test
    void getById_whenFound_returnsOk() {
        // Given
        Preguntas pregunta = samplePregunta();
        when(preguntasService.getById(1L)).thenReturn(Optional.of(pregunta));

        // When
        ResponseEntity<EntityModel<Preguntas>> response = preguntasController.getById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(pregunta, response.getBody().getContent());
        assertTrue(response.getBody().getLink(IanaLinkRelations.SELF).isPresent());
        assertTrue(response.getBody().getLink("listar").isPresent());
        verify(preguntasService, times(1)).getById(1L);
    }

    @Test
    void getById_whenNotFound_returnsNotFound() {
        // Given
        when(preguntasService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<EntityModel<Preguntas>> response = preguntasController.getById(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(preguntasService, times(1)).getById(99L);
    }

    @Test
    void crear_returnsOkWithSavedEntity() {
        // Given
        Preguntas toSave = samplePregunta();
        Preguntas saved = samplePregunta();
        when(preguntasService.guardar(toSave)).thenReturn(saved);

        // When
        ResponseEntity<EntityModel<Preguntas>> response = preguntasController.crear(toSave);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(saved, response.getBody().getContent());
        assertTrue(response.getBody().getLink(IanaLinkRelations.SELF).isPresent());
        verify(preguntasService, times(1)).guardar(toSave);
    }

    @Test
    void actualizar_whenFound_returnsOkAndSetsId() {
        // Given
        Preguntas existing = samplePregunta();
        Preguntas incoming = new Preguntas(null, "Otra pregunta?", 8, 20L);
        when(preguntasService.getById(1L)).thenReturn(Optional.of(existing));
        when(preguntasService.guardar(any(Preguntas.class))).thenReturn(incoming);

        // When
        ResponseEntity<EntityModel<Preguntas>> response = preguntasController.actualizar(1L, incoming);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(incoming, response.getBody().getContent());
        assertTrue(response.getBody().getLink(IanaLinkRelations.SELF).isPresent());
        assertEquals(1L, incoming.getIdPregunta());
        verify(preguntasService, times(1)).getById(1L);
        verify(preguntasService, times(1)).guardar(incoming);
    }

    @Test
    void actualizar_whenNotFound_returnsNotFound() {
        // Given
        Preguntas incoming = new Preguntas(null, "Otra pregunta?", 8, 20L);
        when(preguntasService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<EntityModel<Preguntas>> response = preguntasController.actualizar(99L, incoming);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(preguntasService, times(1)).getById(99L);
        verify(preguntasService, never()).guardar(any(Preguntas.class));
    }

    @Test
    void borrar_whenFound_returnsNoContent() {
        // Given
        when(preguntasService.getById(1L)).thenReturn(Optional.of(samplePregunta()));

        // When
        ResponseEntity<Void> response = preguntasController.borrar(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(preguntasService, times(1)).getById(1L);
        verify(preguntasService, times(1)).borrar(1L);
    }

    @Test
    void borrar_whenNotFound_returnsNotFound() {
        // Given
        when(preguntasService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Void> response = preguntasController.borrar(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(preguntasService, times(1)).getById(99L);
        verify(preguntasService, never()).borrar(any());
    }
}
