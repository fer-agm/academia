package com.academia.clases_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.academia.clases_service.model.Clase;
import com.academia.clases_service.repository.ClaseRepository;

@ExtendWith(MockitoExtension.class)
class ClaseServiceTest {

    @Mock
    private ClaseRepository claseRepository;

    @InjectMocks
    private ClaseService claseService;

    private Clase clase;

    @BeforeEach
    void setUp() {
        // Clase(idClase, nombreClase, contenidoClase, duracionClase, realizada, idCurso)
        clase = new Clase(1L, "Introduccion", "Contenido intro", 60, 0L, 10L);
    }

    @Test
    void getAll_returnsAllClases() {
        // Given
        List<Clase> clases = Arrays.asList(
                clase,
                new Clase(2L, "Avanzado", "Contenido avanzado", 90, 1L, 10L));
        when(claseRepository.findAll()).thenReturn(clases);

        // When
        List<Clase> result = claseService.getAll();

        // Then
        assertEquals(2, result.size());
        assertSame(clases, result);
        verify(claseRepository, times(1)).findAll();
        verifyNoMoreInteractions(claseRepository);
    }

    @Test
    void getAll_returnsEmptyListWhenNoClases() {
        // Given
        when(claseRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Clase> result = claseService.getAll();

        // Then
        assertTrue(result.isEmpty());
        verify(claseRepository).findAll();
    }

    @Test
    void getById_returnsClaseWhenFound() {
        // Given
        when(claseRepository.findById(1L)).thenReturn(Optional.of(clase));

        // When
        Optional<Clase> result = claseService.getById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Introduccion", result.get().getNombreClase());
        assertEquals(10L, result.get().getIdCurso());
        verify(claseRepository).findById(1L);
    }

    @Test
    void getById_returnsEmptyWhenNotFound() {
        // Given
        when(claseRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Clase> result = claseService.getById(99L);

        // Then
        assertFalse(result.isPresent());
        verify(claseRepository).findById(99L);
    }

    @Test
    void getByCurso_returnsClasesForCurso() {
        // Given
        List<Clase> clases = Arrays.asList(
                clase,
                new Clase(2L, "Avanzado", "Contenido avanzado", 90, 1L, 10L));
        when(claseRepository.findByIdCurso(10L)).thenReturn(clases);

        // When
        List<Clase> result = claseService.getByCurso(10L);

        // Then
        assertEquals(2, result.size());
        assertSame(clases, result);
        verify(claseRepository).findByIdCurso(10L);
    }

    @Test
    void getByCurso_returnsEmptyListWhenNoClasesForCurso() {
        // Given
        when(claseRepository.findByIdCurso(999L)).thenReturn(Collections.emptyList());

        // When
        List<Clase> result = claseService.getByCurso(999L);

        // Then
        assertTrue(result.isEmpty());
        verify(claseRepository).findByIdCurso(999L);
    }

    @Test
    void guardar_savesAndReturnsClase() {
        // Given
        Clase toSave = new Clase(null, "Nueva clase", "Contenido nuevo", 45, 0L, 10L);
        Clase saved = new Clase(7L, "Nueva clase", "Contenido nuevo", 45, 0L, 10L);
        when(claseRepository.save(toSave)).thenReturn(saved);

        // When
        Clase result = claseService.guardar(toSave);

        // Then
        assertEquals(7L, result.getIdClase());
        assertEquals("Nueva clase", result.getNombreClase());
        verify(claseRepository).save(toSave);
    }

    @Test
    void borrar_deletesById() {
        // Given
        Long id = 1L;

        // When
        claseService.borrar(id);

        // Then
        verify(claseRepository, times(1)).deleteById(id);
        verify(claseRepository, never()).save(any(Clase.class));
    }
}
