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

import com.academia.clases_service.model.Curso;
import com.academia.clases_service.repository.CursoRepository;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoService cursoService;

    private Curso curso;

    @BeforeEach
    void setUp() {
        // Curso(idCurso, nombreCurso, duracionCurso, descripcionCurso, valorCurso, idCategoria, maxCupos)
        curso = new Curso(1L, "Java Basico", 40, "Curso intro a Java", 19990.0, 5L, 30);
    }

    @Test
    void getAll_returnsAllCursos() {
        // Given
        List<Curso> cursos = Arrays.asList(
                curso,
                new Curso(2L, "Java Avanzado", 60, "Curso avanzado", 29990.0, 5L, 20));
        when(cursoRepository.findAll()).thenReturn(cursos);

        // When
        List<Curso> result = cursoService.getAll();

        // Then
        assertEquals(2, result.size());
        assertSame(cursos, result);
        verify(cursoRepository, times(1)).findAll();
        verifyNoMoreInteractions(cursoRepository);
    }

    @Test
    void getAll_returnsEmptyListWhenNoCursos() {
        // Given
        when(cursoRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Curso> result = cursoService.getAll();

        // Then
        assertTrue(result.isEmpty());
        verify(cursoRepository).findAll();
    }

    @Test
    void getById_returnsCursoWhenFound() {
        // Given
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        // When
        Optional<Curso> result = cursoService.getById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Java Basico", result.get().getNombreCurso());
        assertEquals(5L, result.get().getIdCategoria());
        verify(cursoRepository).findById(1L);
    }

    @Test
    void getById_returnsEmptyWhenNotFound() {
        // Given
        when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Curso> result = cursoService.getById(99L);

        // Then
        assertFalse(result.isPresent());
        verify(cursoRepository).findById(99L);
    }

    @Test
    void getByCategoria_returnsCursosForCategoria() {
        // Given
        List<Curso> cursos = Arrays.asList(
                curso,
                new Curso(2L, "Java Avanzado", 60, "Curso avanzado", 29990.0, 5L, 20));
        when(cursoRepository.findByIdCategoria(5L)).thenReturn(cursos);

        // When
        List<Curso> result = cursoService.getByCategoria(5L);

        // Then
        assertEquals(2, result.size());
        assertSame(cursos, result);
        verify(cursoRepository).findByIdCategoria(5L);
    }

    @Test
    void getByCategoria_returnsEmptyListWhenNoCursosForCategoria() {
        // Given
        when(cursoRepository.findByIdCategoria(999L)).thenReturn(Collections.emptyList());

        // When
        List<Curso> result = cursoService.getByCategoria(999L);

        // Then
        assertTrue(result.isEmpty());
        verify(cursoRepository).findByIdCategoria(999L);
    }

    @Test
    void guardar_savesAndReturnsCurso() {
        // Given
        Curso toSave = new Curso(null, "Python", 50, "Curso de Python", 24990.0, 5L, 25);
        Curso saved = new Curso(9L, "Python", 50, "Curso de Python", 24990.0, 5L, 25);
        when(cursoRepository.save(toSave)).thenReturn(saved);

        // When
        Curso result = cursoService.guardar(toSave);

        // Then
        assertEquals(9L, result.getIdCurso());
        assertEquals("Python", result.getNombreCurso());
        verify(cursoRepository).save(toSave);
    }

    @Test
    void borrar_deletesById() {
        // Given
        Long id = 1L;

        // When
        cursoService.borrar(id);

        // Then
        verify(cursoRepository, times(1)).deleteById(id);
        verify(cursoRepository, never()).save(any(Curso.class));
    }
}
