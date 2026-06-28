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

import com.academia.evaluaciones_service.model.Alternativas;
import com.academia.evaluaciones_service.service.AlternativasService;

@ExtendWith(MockitoExtension.class)
class AlternativasControllerTest {

    @Mock
    private AlternativasService alternativasService;

    @InjectMocks
    private AlternativasController alternativasController;

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    private Alternativas sampleAlternativa() {
        return new Alternativas(1L, "Respuesta A", true, 10L);
    }

    @Test
    void getAll_returnsOkWithList() {
        // Given
        List<Alternativas> alternativas = List.of(sampleAlternativa());
        when(alternativasService.getAll()).thenReturn(alternativas);

        // When
        CollectionModel<EntityModel<Alternativas>> response = alternativasController.getAll();

        // Then
        assertEquals(1, response.getContent().size());
        EntityModel<Alternativas> item = response.getContent().iterator().next();
        assertSame(alternativas.get(0), item.getContent());
        assertTrue(item.getLink(IanaLinkRelations.SELF).isPresent());
        assertTrue(item.getLink("listar").isPresent());
        assertTrue(response.getLink(IanaLinkRelations.SELF).isPresent());
        verify(alternativasService, times(1)).getAll();
    }

    @Test
    void getById_whenFound_returnsOk() {
        // Given
        Alternativas alternativa = sampleAlternativa();
        when(alternativasService.getById(1L)).thenReturn(Optional.of(alternativa));

        // When
        ResponseEntity<EntityModel<Alternativas>> response = alternativasController.getById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(alternativa, response.getBody().getContent());
        assertTrue(response.getBody().getLink(IanaLinkRelations.SELF).isPresent());
        assertTrue(response.getBody().getLink("listar").isPresent());
        verify(alternativasService, times(1)).getById(1L);
    }

    @Test
    void getById_whenNotFound_returnsNotFound() {
        // Given
        when(alternativasService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<EntityModel<Alternativas>> response = alternativasController.getById(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(alternativasService, times(1)).getById(99L);
    }

    @Test
    void crear_returnsOkWithSavedEntity() {
        // Given
        Alternativas toSave = sampleAlternativa();
        Alternativas saved = sampleAlternativa();
        when(alternativasService.guardar(toSave)).thenReturn(saved);

        // When
        ResponseEntity<EntityModel<Alternativas>> response = alternativasController.crear(toSave);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(saved, response.getBody().getContent());
        assertTrue(response.getBody().getLink(IanaLinkRelations.SELF).isPresent());
        verify(alternativasService, times(1)).guardar(toSave);
    }

    @Test
    void actualizar_whenFound_returnsOkAndSetsId() {
        // Given
        Alternativas existing = sampleAlternativa();
        Alternativas incoming = new Alternativas(null, "Respuesta B", false, 10L);
        when(alternativasService.getById(1L)).thenReturn(Optional.of(existing));
        when(alternativasService.guardar(any(Alternativas.class))).thenReturn(incoming);

        // When
        ResponseEntity<EntityModel<Alternativas>> response = alternativasController.actualizar(1L, incoming);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(incoming, response.getBody().getContent());
        assertTrue(response.getBody().getLink(IanaLinkRelations.SELF).isPresent());
        assertEquals(1L, incoming.getIdAlternativa());
        verify(alternativasService, times(1)).getById(1L);
        verify(alternativasService, times(1)).guardar(incoming);
    }

    @Test
    void actualizar_whenNotFound_returnsNotFound() {
        // Given
        Alternativas incoming = new Alternativas(null, "Respuesta B", false, 10L);
        when(alternativasService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<EntityModel<Alternativas>> response = alternativasController.actualizar(99L, incoming);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(alternativasService, times(1)).getById(99L);
        verify(alternativasService, never()).guardar(any(Alternativas.class));
    }

    @Test
    void borrar_whenFound_returnsNoContent() {
        // Given
        when(alternativasService.getById(1L)).thenReturn(Optional.of(sampleAlternativa()));

        // When
        ResponseEntity<Void> response = alternativasController.borrar(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(alternativasService, times(1)).getById(1L);
        verify(alternativasService, times(1)).borrar(1L);
    }

    @Test
    void borrar_whenNotFound_returnsNotFound() {
        // Given
        when(alternativasService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Void> response = alternativasController.borrar(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(alternativasService, times(1)).getById(99L);
        verify(alternativasService, never()).borrar(any());
    }
}
