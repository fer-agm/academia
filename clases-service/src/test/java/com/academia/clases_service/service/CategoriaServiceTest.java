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

import com.academia.clases_service.model.Categoria;
import com.academia.clases_service.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Programacion", "Cursos de programacion");
    }

    @Test
    void getAll_returnsAllCategorias() {
        // Given
        List<Categoria> categorias = Arrays.asList(
                categoria,
                new Categoria(2L, "Diseno", "Cursos de diseno"));
        when(categoriaRepository.findAll()).thenReturn(categorias);

        // When
        List<Categoria> result = categoriaService.getAll();

        // Then
        assertEquals(2, result.size());
        assertSame(categorias, result);
        verify(categoriaRepository, times(1)).findAll();
        verifyNoMoreInteractions(categoriaRepository);
    }

    @Test
    void getAll_returnsEmptyListWhenNoCategorias() {
        // Given
        when(categoriaRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Categoria> result = categoriaService.getAll();

        // Then
        assertTrue(result.isEmpty());
        verify(categoriaRepository).findAll();
    }

    @Test
    void getById_returnsCategoriaWhenFound() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // When
        Optional<Categoria> result = categoriaService.getById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Programacion", result.get().getNombreCategoria());
        assertEquals(1L, result.get().getIdCategoria());
        verify(categoriaRepository).findById(1L);
    }

    @Test
    void getById_returnsEmptyWhenNotFound() {
        // Given
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Categoria> result = categoriaService.getById(99L);

        // Then
        assertFalse(result.isPresent());
        verify(categoriaRepository).findById(99L);
    }

    @Test
    void guardar_savesAndReturnsCategoria() {
        // Given
        Categoria toSave = new Categoria(null, "Marketing", "Cursos de marketing");
        Categoria saved = new Categoria(5L, "Marketing", "Cursos de marketing");
        when(categoriaRepository.save(toSave)).thenReturn(saved);

        // When
        Categoria result = categoriaService.guardar(toSave);

        // Then
        assertEquals(5L, result.getIdCategoria());
        assertEquals("Marketing", result.getNombreCategoria());
        verify(categoriaRepository).save(toSave);
    }

    @Test
    void borrar_deletesById() {
        // Given
        Long id = 1L;

        // When
        categoriaService.borrar(id);

        // Then
        verify(categoriaRepository, times(1)).deleteById(id);
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }
}
