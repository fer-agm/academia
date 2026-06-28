package com.academia.inscripciones_service.service;

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
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.academia.inscripciones_service.model.Cupos;
import com.academia.inscripciones_service.repository.CuposRepository;

@ExtendWith(MockitoExtension.class)
class CuposServiceTest {

    @Mock
    private CuposRepository cuposRepository;

    @InjectMocks
    private CuposService cuposService;

    private Cupos cupo;

    @BeforeEach
    void setUp() {
        // Given: a baseline Cupos used across tests
        cupo = new Cupos(1L, 100L, 30, 10);
    }

    @Test
    @DisplayName("listarTodos devuelve todos los cupos del repositorio")
    void listarTodos_devuelveLista() {
        // Given
        Cupos otro = new Cupos(2L, 200L, 40, 5);
        List<Cupos> esperados = Arrays.asList(cupo, otro);
        when(cuposRepository.findAll()).thenReturn(esperados);

        // When
        List<Cupos> resultado = cuposService.listarTodos();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(esperados, resultado);
        verify(cuposRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("listarTodos devuelve lista vacia cuando no hay cupos")
    void listarTodos_vacia() {
        // Given
        when(cuposRepository.findAll()).thenReturn(List.of());

        // When
        List<Cupos> resultado = cuposService.listarTodos();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(cuposRepository).findAll();
    }

    @Test
    @DisplayName("buscarPorId devuelve el cupo cuando existe")
    void buscarPorId_encontrado() {
        // Given
        when(cuposRepository.findById(1L)).thenReturn(Optional.of(cupo));

        // When
        Cupos resultado = cuposService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertSame(cupo, resultado);
        verify(cuposRepository).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId devuelve null cuando no existe")
    void buscarPorId_noEncontrado() {
        // Given
        when(cuposRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Cupos resultado = cuposService.buscarPorId(99L);

        // Then
        assertNull(resultado);
        verify(cuposRepository).findById(99L);
    }

    @Test
    @DisplayName("guardarCupo persiste y devuelve el cupo guardado")
    void guardarCupo_ok() {
        // Given
        when(cuposRepository.save(cupo)).thenReturn(cupo);

        // When
        Cupos resultado = cuposService.guardarCupo(cupo);

        // Then
        assertNotNull(resultado);
        assertEquals(cupo.getId_cupo(), resultado.getId_cupo());
        verify(cuposRepository).save(cupo);
    }

    @Test
    @DisplayName("obtenerPorCurso devuelve el cupo asociado al curso")
    void obtenerPorCurso_encontrado() {
        // Given
        when(cuposRepository.findByIdCurso(100L)).thenReturn(Optional.of(cupo));

        // When
        Cupos resultado = cuposService.obtenerPorCurso(100L);

        // Then
        assertNotNull(resultado);
        assertEquals(100L, resultado.getIdCurso());
        verify(cuposRepository).findByIdCurso(100L);
    }

    @Test
    @DisplayName("obtenerPorCurso devuelve null cuando el curso no tiene cupo")
    void obtenerPorCurso_noEncontrado() {
        // Given
        when(cuposRepository.findByIdCurso(500L)).thenReturn(Optional.empty());

        // When
        Cupos resultado = cuposService.obtenerPorCurso(500L);

        // Then
        assertNull(resultado);
        verify(cuposRepository).findByIdCurso(500L);
    }

    @Test
    @DisplayName("actualizar modifica y guarda el cupo cuando existe")
    void actualizar_encontrado() {
        // Given
        Cupos datos = new Cupos(null, null, 50, 20);
        when(cuposRepository.findById(1L)).thenReturn(Optional.of(cupo));
        when(cuposRepository.save(any(Cupos.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Cupos resultado = cuposService.actualizar(1L, datos);

        // Then
        assertNotNull(resultado);
        assertEquals(50, resultado.getNum_maximo());
        assertEquals(20, resultado.getNum_disponible());
        verify(cuposRepository).findById(1L);
        verify(cuposRepository).save(cupo);
    }

    @Test
    @DisplayName("actualizar devuelve null y no guarda cuando el cupo no existe")
    void actualizar_noEncontrado() {
        // Given
        Cupos datos = new Cupos(null, null, 50, 20);
        when(cuposRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Cupos resultado = cuposService.actualizar(99L, datos);

        // Then
        assertNull(resultado);
        verify(cuposRepository).findById(99L);
        verify(cuposRepository, never()).save(any(Cupos.class));
    }

    @Test
    @DisplayName("eliminar devuelve true y borra cuando el cupo existe")
    void eliminar_existente() {
        // Given
        when(cuposRepository.existsById(1L)).thenReturn(true);

        // When
        boolean resultado = cuposService.eliminar(1L);

        // Then
        assertTrue(resultado);
        verify(cuposRepository).existsById(1L);
        verify(cuposRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar devuelve false y no borra cuando el cupo no existe")
    void eliminar_inexistente() {
        // Given
        when(cuposRepository.existsById(99L)).thenReturn(false);

        // When
        boolean resultado = cuposService.eliminar(99L);

        // Then
        assertFalse(resultado);
        verify(cuposRepository).existsById(99L);
        verify(cuposRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("reducirCupo decrementa el disponible cuando hay cupos")
    void reducirCupo_conDisponibles() {
        // Given
        when(cuposRepository.findByIdCurso(100L)).thenReturn(Optional.of(cupo));
        when(cuposRepository.save(any(Cupos.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        boolean resultado = cuposService.reducirCupo(100L);

        // Then
        assertTrue(resultado);
        assertEquals(9, cupo.getNum_disponible());
        verify(cuposRepository).findByIdCurso(100L);
        verify(cuposRepository).save(cupo);
    }

    @Test
    @DisplayName("reducirCupo devuelve false cuando no quedan cupos disponibles")
    void reducirCupo_sinDisponibles() {
        // Given
        Cupos lleno = new Cupos(3L, 300L, 30, 0);
        when(cuposRepository.findByIdCurso(300L)).thenReturn(Optional.of(lleno));

        // When
        boolean resultado = cuposService.reducirCupo(300L);

        // Then
        assertFalse(resultado);
        assertEquals(0, lleno.getNum_disponible());
        verify(cuposRepository).findByIdCurso(300L);
        verify(cuposRepository, never()).save(any(Cupos.class));
    }

    @Test
    @DisplayName("reducirCupo devuelve false cuando el curso no tiene cupo")
    void reducirCupo_cupoNulo() {
        // Given
        when(cuposRepository.findByIdCurso(404L)).thenReturn(Optional.empty());

        // When
        boolean resultado = cuposService.reducirCupo(404L);

        // Then
        assertFalse(resultado);
        verify(cuposRepository).findByIdCurso(404L);
        verify(cuposRepository, never()).save(any(Cupos.class));
    }
}
