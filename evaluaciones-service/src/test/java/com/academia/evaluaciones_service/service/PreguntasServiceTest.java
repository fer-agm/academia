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

import com.academia.evaluaciones_service.model.Preguntas;
import com.academia.evaluaciones_service.repository.PreguntasRepository;

@ExtendWith(MockitoExtension.class)
class PreguntasServiceTest {

    @Mock
    private PreguntasRepository preguntasRepository;

    @InjectMocks
    private PreguntasService preguntasService;

    private Preguntas buildPregunta() {
        return new Preguntas(1L, "Cual es la capital?", 5, 10L);
    }

    @Test
    void getAll_returnsListFromRepository() {
        // Given
        Preguntas p1 = buildPregunta();
        Preguntas p2 = new Preguntas(2L, "Segunda pregunta?", 8, 10L);
        when(preguntasRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        // When
        List<Preguntas> result = preguntasService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Cual es la capital?", result.get(0).getEnunciado());
        verify(preguntasRepository, times(1)).findAll();
    }

    @Test
    void getAll_returnsEmptyListWhenNoPreguntas() {
        // Given
        when(preguntasRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Preguntas> result = preguntasService.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(preguntasRepository, times(1)).findAll();
    }

    @Test
    void getById_returnsPreguntaWhenFound() {
        // Given
        Preguntas pregunta = buildPregunta();
        when(preguntasRepository.findById(1L)).thenReturn(Optional.of(pregunta));

        // When
        Optional<Preguntas> result = preguntasService.getById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getIdPregunta());
        assertEquals(5, result.get().getPuntaje());
        assertEquals(10L, result.get().getIdEvaluacion());
        verify(preguntasRepository, times(1)).findById(1L);
    }

    @Test
    void getById_returnsEmptyWhenNotFound() {
        // Given
        when(preguntasRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Preguntas> result = preguntasService.getById(99L);

        // Then
        assertFalse(result.isPresent());
        verify(preguntasRepository, times(1)).findById(99L);
    }

    @Test
    void guardar_savesAndReturnsPregunta() {
        // Given
        Preguntas toSave = new Preguntas(null, "Nueva pregunta?", 3, 20L);
        Preguntas saved = new Preguntas(1L, "Nueva pregunta?", 3, 20L);
        when(preguntasRepository.save(toSave)).thenReturn(saved);

        // When
        Preguntas result = preguntasService.guardar(toSave);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getIdPregunta());
        assertEquals("Nueva pregunta?", result.getEnunciado());
        verify(preguntasRepository, times(1)).save(toSave);
    }

    @Test
    void guardar_returnsSameInstanceFromRepository() {
        // Given
        Preguntas pregunta = buildPregunta();
        when(preguntasRepository.save(any(Preguntas.class))).thenReturn(pregunta);

        // When
        Preguntas result = preguntasService.guardar(pregunta);

        // Then
        assertSame(pregunta, result);
        verify(preguntasRepository, times(1)).save(pregunta);
    }

    @Test
    void borrar_deletesByIdAndReturnsVoid() {
        // Given
        Long id = 1L;

        // When
        preguntasService.borrar(id);

        // Then
        verify(preguntasRepository, times(1)).deleteById(id);
        verify(preguntasRepository, never()).findById(any());
    }
}
