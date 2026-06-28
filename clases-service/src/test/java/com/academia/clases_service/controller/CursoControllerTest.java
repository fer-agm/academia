package com.academia.clases_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.academia.clases_service.model.Curso;
import com.academia.clases_service.service.CursoService;

@ExtendWith(MockitoExtension.class)
class CursoControllerTest {

    @Mock
    private CursoService cursoService;

    @InjectMocks
    private CursoController cursoController;

    private Curso curso;

    @BeforeEach
    void setUp() {
        // Required because the controller builds HATEOAS links via linkTo(methodOn(...))
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
        // Curso(idCurso, nombreCurso, duracionCurso, descripcionCurso, valorCurso, idCategoria, maxCupos)
        curso = new Curso(1L, "Java Basico", 40, "Curso introductorio", 100000.0, 10L, 30);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getAll_returnsOkWithCollectionModel() {
        // Given
        List<Curso> cursos = Arrays.asList(
                curso,
                new Curso(2L, "Python", 50, "Curso de python", 120000.0, 10L, 25));
        when(cursoService.getAll()).thenReturn(cursos);

        // When
        ResponseEntity<CollectionModel<EntityModel<Curso>>> response = cursoController.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Collection<EntityModel<Curso>> content = response.getBody().getContent();
        assertEquals(2, content.size());
        assertTrue(response.getBody().hasLinks());
        assertTrue(response.getBody().getLink("self").isPresent());
        content.forEach(item -> assertTrue(item.hasLink("self")));
        verify(cursoService, times(1)).getAll();
    }

    @Test
    void getById_returnsOkWhenFound() {
        // Given
        when(cursoService.getById(1L)).thenReturn(Optional.of(curso));

        // When
        ResponseEntity<EntityModel<Curso>> response = cursoController.getById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(curso, response.getBody().getContent());
        assertTrue(response.getBody().hasLink("self"));
        assertTrue(response.getBody().hasLink("listar"));
        verify(cursoService).getById(1L);
    }

    @Test
    void getById_returnsNotFoundWhenMissing() {
        // Given
        when(cursoService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<EntityModel<Curso>> response = cursoController.getById(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(cursoService).getById(99L);
    }

    @Test
    void getByCategoria_returnsOkWithCollectionModel() {
        // Given
        List<Curso> cursos = Arrays.asList(curso);
        when(cursoService.getByCategoria(10L)).thenReturn(cursos);

        // When
        ResponseEntity<CollectionModel<EntityModel<Curso>>> response = cursoController.getByCategoria(10L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertTrue(response.getBody().getLink("self").isPresent());
        verify(cursoService).getByCategoria(10L);
    }

    @Test
    void getByCategoria_returnsOkWithEmptyCollection() {
        // Given
        when(cursoService.getByCategoria(999L)).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<CollectionModel<EntityModel<Curso>>> response = cursoController.getByCategoria(999L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
        verify(cursoService).getByCategoria(999L);
    }

    @Test
    void existe_returnsTrueWhenFound() {
        // Given
        when(cursoService.getById(1L)).thenReturn(Optional.of(curso));

        // When
        ResponseEntity<Boolean> response = cursoController.existe(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(cursoService).getById(1L);
    }

    @Test
    void existe_returnsFalseWhenMissing() {
        // Given
        when(cursoService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Boolean> response = cursoController.existe(99L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
        verify(cursoService).getById(99L);
    }

    @Test
    void crear_returnsOkWithSavedCurso() {
        // Given
        Curso toSave = new Curso(null, "Nuevo Curso", 20, "Desc", 50000.0, 10L, 15);
        Curso saved = new Curso(8L, "Nuevo Curso", 20, "Desc", 50000.0, 10L, 15);
        when(cursoService.guardar(toSave)).thenReturn(saved);

        // When
        ResponseEntity<EntityModel<Curso>> response = cursoController.crear(toSave);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(saved, response.getBody().getContent());
        assertTrue(response.getBody().hasLink("self"));
        verify(cursoService).guardar(toSave);
    }

    @Test
    void actualizar_returnsOkWhenFound() {
        // Given
        Curso input = new Curso(null, "Java Avanzado", 60, "Actualizado", 150000.0, 10L, 20);
        when(cursoService.getById(1L)).thenReturn(Optional.of(curso));
        when(cursoService.guardar(any(Curso.class))).thenReturn(input);

        // When
        ResponseEntity<EntityModel<Curso>> response = cursoController.actualizar(1L, input);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(input, response.getBody().getContent());
        assertTrue(response.getBody().hasLink("self"));
        // The controller forces the path id onto the entity before saving
        assertEquals(1L, input.getIdCurso());
        verify(cursoService).getById(1L);
        verify(cursoService).guardar(input);
    }

    @Test
    void actualizar_returnsNotFoundWhenMissing() {
        // Given
        Curso input = new Curso(null, "Java Avanzado", 60, "Actualizado", 150000.0, 10L, 20);
        when(cursoService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<EntityModel<Curso>> response = cursoController.actualizar(99L, input);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(cursoService).getById(99L);
        verify(cursoService, never()).guardar(any(Curso.class));
    }

    @Test
    void borrar_returnsNoContentWhenFound() {
        // Given
        when(cursoService.getById(1L)).thenReturn(Optional.of(curso));

        // When
        ResponseEntity<Void> response = cursoController.borrar(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(cursoService).getById(1L);
        verify(cursoService).borrar(1L);
    }

    @Test
    void borrar_returnsNotFoundWhenMissing() {
        // Given
        when(cursoService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Void> response = cursoController.borrar(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(cursoService).getById(99L);
        verify(cursoService, never()).borrar(any(Long.class));
    }
}
