package com.academia.clases_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
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

import com.academia.clases_service.model.Categoria;
import com.academia.clases_service.service.CategoriaService;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaController categoriaController;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        // Required because the controller builds HATEOAS links via linkTo(methodOn(...))
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
        // Categoria(idCategoria, nombreCategoria, descripcionCategoria)
        categoria = new Categoria(1L, "Programacion", "Cursos de programacion");
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getAll_returnsOkWithCollectionModel() {
        // Given
        List<Categoria> categorias = Arrays.asList(
                categoria,
                new Categoria(2L, "Diseno", "Cursos de diseno"));
        when(categoriaService.getAll()).thenReturn(categorias);

        // When
        ResponseEntity<CollectionModel<EntityModel<Categoria>>> response = categoriaController.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Collection<EntityModel<Categoria>> content = response.getBody().getContent();
        assertEquals(2, content.size());
        // CollectionModel carries the self link added by the controller
        assertTrue(response.getBody().hasLinks());
        assertTrue(response.getBody().getLink("self").isPresent());
        // each item carries its own self link
        content.forEach(item -> assertTrue(item.hasLink("self")));
        verify(categoriaService, times(1)).getAll();
    }

    @Test
    void getById_returnsOkWhenFound() {
        // Given
        when(categoriaService.getById(1L)).thenReturn(Optional.of(categoria));

        // When
        ResponseEntity<EntityModel<Categoria>> response = categoriaController.getById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(categoria, response.getBody().getContent());
        assertTrue(response.getBody().hasLink("self"));
        assertTrue(response.getBody().hasLink("listar"));
        verify(categoriaService).getById(1L);
    }

    @Test
    void getById_returnsNotFoundWhenMissing() {
        // Given
        when(categoriaService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<EntityModel<Categoria>> response = categoriaController.getById(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(categoriaService).getById(99L);
    }

    @Test
    void crear_returnsOkWithSavedCategoria() {
        // Given
        Categoria toSave = new Categoria(null, "Marketing", "Cursos de marketing");
        Categoria saved = new Categoria(5L, "Marketing", "Cursos de marketing");
        when(categoriaService.guardar(toSave)).thenReturn(saved);

        // When
        ResponseEntity<EntityModel<Categoria>> response = categoriaController.crear(toSave);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(saved, response.getBody().getContent());
        assertTrue(response.getBody().hasLink("self"));
        verify(categoriaService).guardar(toSave);
    }

    @Test
    void actualizar_returnsOkWhenFound() {
        // Given
        Categoria input = new Categoria(null, "Programacion Avanzada", "Actualizada");
        when(categoriaService.getById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaService.guardar(any(Categoria.class))).thenReturn(input);

        // When
        ResponseEntity<EntityModel<Categoria>> response = categoriaController.actualizar(1L, input);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(input, response.getBody().getContent());
        assertTrue(response.getBody().hasLink("self"));
        // The controller forces the path id onto the entity before saving
        assertEquals(1L, input.getIdCategoria());
        verify(categoriaService).getById(1L);
        verify(categoriaService).guardar(input);
    }

    @Test
    void actualizar_returnsNotFoundWhenMissing() {
        // Given
        Categoria input = new Categoria(null, "Programacion Avanzada", "Actualizada");
        when(categoriaService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<EntityModel<Categoria>> response = categoriaController.actualizar(99L, input);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(categoriaService).getById(99L);
        verify(categoriaService, never()).guardar(any(Categoria.class));
    }

    @Test
    void borrar_returnsNoContentWhenFound() {
        // Given
        when(categoriaService.getById(1L)).thenReturn(Optional.of(categoria));

        // When
        ResponseEntity<Void> response = categoriaController.borrar(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody() == null);
        verify(categoriaService).getById(1L);
        verify(categoriaService).borrar(1L);
    }

    @Test
    void borrar_returnsNotFoundWhenMissing() {
        // Given
        when(categoriaService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Void> response = categoriaController.borrar(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(categoriaService).getById(99L);
        verify(categoriaService, never()).borrar(eq(99L));
    }
}
