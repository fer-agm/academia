package com.academia.evaluaciones_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.academia.evaluaciones_service.model.Alternativas;
import com.academia.evaluaciones_service.repository.AlternativasRepository;

@ExtendWith(MockitoExtension.class)
class AlternativasServiceTest {

    @Mock
    private AlternativasRepository alternativasRepository;

    @InjectMocks
    private AlternativasService alternativasService;

    private Alternativas buildAlternativa() {
        return new Alternativas(1L, "Respuesta A", true, 10L);
    }

    @Test
    void getAll_returnsListFromRepository() {
        // Given
        Alternativas a1 = buildAlternativa();
        Alternativas a2 = new Alternativas(2L, "Respuesta B", false, 10L);
        when(alternativasRepository.findAll()).thenReturn(Arrays.asList(a1, a2));

        // When
        List<Alternativas> result = alternativasService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Respuesta A", result.get(0).getTexto());
        verify(alternativasRepository, times(1)).findAll();
    }

    @Test
    void getAll_returnsEmptyListWhenNoAlternativas() {
        // Given
        when(alternativasRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Alternativas> result = alternativasService.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(alternativasRepository, times(1)).findAll();
    }

    @Test
    void getById_returnsAlternativaWhenFound() {
        // Given
        Alternativas alternativa = buildAlternativa();
        when(alternativasRepository.findById(1L)).thenReturn(Optional.of(alternativa));

        // When
        Optional<Alternativas> result = alternativasService.getById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getIdAlternativa());
        assertEquals("Respuesta A", result.get().getTexto());
        verify(alternativasRepository, times(1)).findById(1L);
    }

    @Test
    void getById_returnsEmptyWhenNotFound() {
        // Given
        when(alternativasRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Alternativas> result = alternativasService.getById(99L);

        // Then
        assertFalse(result.isPresent());
        verify(alternativasRepository, times(1)).findById(99L);
    }

    @Test
    void guardar_savesAndReturnsAlternativa() {
        // Given
        Alternativas toSave = new Alternativas(null, "Nueva", true, 5L);
        Alternativas saved = new Alternativas(1L, "Nueva", true, 5L);
        when(alternativasRepository.save(toSave)).thenReturn(saved);

        // When
        Alternativas result = alternativasService.guardar(toSave);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getIdAlternativa());
        assertEquals("Nueva", result.getTexto());
        verify(alternativasRepository, times(1)).save(toSave);
    }

    @Test
    void guardar_returnsSameInstanceFromRepository() {
        // Given
        Alternativas alternativa = buildAlternativa();
        when(alternativasRepository.save(any(Alternativas.class))).thenReturn(alternativa);

        // When
        Alternativas result = alternativasService.guardar(alternativa);

        // Then
        assertSame(alternativa, result);
        verify(alternativasRepository, times(1)).save(alternativa);
    }

    @Test
    void borrar_deletesByIdAndReturnsVoid() {
        // Given
        Long id = 1L;

        // When
        alternativasService.borrar(id);

        // Then
        verify(alternativasRepository, times(1)).deleteById(id);
        verify(alternativasRepository, never()).findById(any());
    }
}
