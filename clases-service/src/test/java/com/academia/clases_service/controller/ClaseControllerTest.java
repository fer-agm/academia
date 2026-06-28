package com.academia.clases_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.academia.clases_service.dto.ClaseDTO;
import com.academia.clases_service.model.Clase;
import com.academia.clases_service.service.ClaseService;

@ExtendWith(MockitoExtension.class)
class ClaseControllerTest {

    @Mock
    private ClaseService claseService;

    @InjectMocks
    private ClaseController claseController;

    private Clase clase;

    @BeforeEach
    void setUp() {
        // Required because the controller builds HATEOAS links via linkTo(methodOn(...))
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
        // Clase(idClase, nombreClase, contenidoClase, duracionClase, realizada, idCurso)
        clase = new Clase(1L, "Introduccion", "Contenido intro", 60, 0L, 10L);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getAll_returnsOkWithCollectionModel() {
        // Given
        List<Clase> clases = Arrays.asList(
                clase,
                new Clase(2L, "Avanzado", "Contenido avanzado", 90, 1L, 10L));
        when(claseService.getAll()).thenReturn(clases);

        // When
        ResponseEntity<CollectionModel<ClaseDTO>> response = claseController.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Collection<ClaseDTO> content = response.getBody().getContent();
        assertEquals(2, content.size());
        // CollectionModel carries the self link added by the controller
        assertTrue(response.getBody().getLink("self").isPresent());
        verify(claseService, times(1)).getAll();
    }

    @Test
    void getAll_returnsOkWithEmptyCollection() {
        // Given
        when(claseService.getAll()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<CollectionModel<ClaseDTO>> response = claseController.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
        verify(claseService).getAll();
    }

    @Test
    void getById_returnsOkWhenFound() {
        // Given
        when(claseService.getById(1L)).thenReturn(Optional.of(clase));

        // When
        ResponseEntity<ClaseDTO> response = claseController.getById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getIdClase());
        assertEquals("Introduccion", response.getBody().getNombreClase());
        // HATEOAS self link is added by agregarLinks
        assertTrue(response.getBody().getLink("self").isPresent());
        verify(claseService).getById(1L);
    }

    @Test
    void getById_returnsNotFoundWhenMissing() {
        // Given
        when(claseService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<ClaseDTO> response = claseController.getById(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(claseService).getById(99L);
    }

    @Test
    void getByCurso_returnsOkWithCollectionModel() {
        // Given
        List<Clase> clases = Arrays.asList(
                clase,
                new Clase(2L, "Avanzado", "Contenido avanzado", 90, 1L, 10L));
        when(claseService.getByCurso(10L)).thenReturn(clases);

        // When
        ResponseEntity<CollectionModel<ClaseDTO>> response = claseController.getByCurso(10L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        assertTrue(response.getBody().getLink("self").isPresent());
        verify(claseService).getByCurso(10L);
    }

    @Test
    void getByCurso_returnsOkWithEmptyCollection() {
        // Given
        when(claseService.getByCurso(999L)).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<CollectionModel<ClaseDTO>> response = claseController.getByCurso(999L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
        verify(claseService).getByCurso(999L);
    }

    @Test
    void crear_returnsCreatedWithBodyAndLocation() {
        // Given
        ClaseDTO input = ClaseDTO.builder()
                .nombreClase("Nueva clase")
                .contenidoClase("Contenido nuevo")
                .duracionClase(45)
                .realizada(0L)
                .idCurso(10L)
                .build();
        Clase saved = new Clase(7L, "Nueva clase", "Contenido nuevo", 45, 0L, 10L);
        when(claseService.guardar(any(Clase.class))).thenReturn(saved);

        // When
        ResponseEntity<ClaseDTO> response = claseController.crear(input);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(7L, response.getBody().getIdClase());
        // created(...) uses the self link URI as Location header
        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getBody().getLink("self").isPresent());
        verify(claseService).guardar(any(Clase.class));
    }

    @Test
    void actualizar_returnsOkWhenFound() {
        // Given
        ClaseDTO input = ClaseDTO.builder()
                .nombreClase("Actualizada")
                .contenidoClase("Contenido actualizado")
                .duracionClase(70)
                .realizada(1L)
                .idCurso(10L)
                .build();
        Clase actualizada = new Clase(1L, "Actualizada", "Contenido actualizado", 70, 1L, 10L);
        when(claseService.getById(1L)).thenReturn(Optional.of(clase));
        when(claseService.guardar(any(Clase.class))).thenReturn(actualizada);

        // When
        ResponseEntity<ClaseDTO> response = claseController.actualizar(1L, input);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getIdClase());
        assertEquals("Actualizada", response.getBody().getNombreClase());
        // The controller forces the path id onto the DTO before mapping to model
        assertEquals(1L, input.getIdClase());
        verify(claseService).getById(1L);
        verify(claseService).guardar(any(Clase.class));
    }

    @Test
    void actualizar_returnsNotFoundWhenMissing() {
        // Given
        ClaseDTO input = ClaseDTO.builder()
                .nombreClase("Actualizada")
                .contenidoClase("Contenido actualizado")
                .duracionClase(70)
                .realizada(1L)
                .idCurso(10L)
                .build();
        when(claseService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<ClaseDTO> response = claseController.actualizar(99L, input);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(claseService).getById(99L);
        verify(claseService, never()).guardar(any(Clase.class));
    }

    @Test
    void borrar_returnsNoContentWhenFound() {
        // Given
        when(claseService.getById(1L)).thenReturn(Optional.of(clase));

        // When
        ResponseEntity<Void> response = claseController.borrar(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(claseService).getById(1L);
        verify(claseService).borrar(1L);
    }

    @Test
    void borrar_returnsNotFoundWhenMissing() {
        // Given
        when(claseService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Void> response = claseController.borrar(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(claseService).getById(99L);
        verify(claseService, never()).borrar(any(Long.class));
    }
}
