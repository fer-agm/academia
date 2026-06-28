package com.academia.calificaciones_service.service;

import com.academia.calificaciones_service.dto.PromedioDTO;
import com.academia.calificaciones_service.model.Promedio;
import com.academia.calificaciones_service.repository.PromedioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Pure Mockito unit tests for {@link PromedioService}.
 * No Spring context, no database.
 */
@ExtendWith(MockitoExtension.class)
class PromedioServiceTest {

    @Mock
    private PromedioRepository repository;

    @InjectMocks
    private PromedioService service;

    // Promedio entity constructor order: (idPromedio, idEstudiante, idCurso, promedioGeneral, totalEvaluaciones)
    private Promedio buildEntity(Long id) {
        return new Promedio(id, "EST-001", 100L, 5.8, 4);
    }

    @Test
    void getAll_returnsMappedDtoList() {
        // Given
        Promedio entity1 = new Promedio(1L, "EST-001", 100L, 5.8, 4);
        Promedio entity2 = new Promedio(2L, "EST-002", 200L, 6.2, 3);
        when(repository.findAll()).thenReturn(List.of(entity1, entity2));

        // When
        List<PromedioDTO> result = service.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // convertToDTO maps: (idPromedio, idCurso, idEstudiante, promedioGeneral, totalEvaluaciones)
        PromedioDTO dto1 = result.get(0);
        assertEquals(1L, dto1.getIdPromedio());
        assertEquals(100L, dto1.getIdCurso());
        assertEquals("EST-001", dto1.getIdEstudiante());
        assertEquals(5.8, dto1.getPromedioGeneral());
        assertEquals(4, dto1.getTotalEvaluaciones());

        PromedioDTO dto2 = result.get(1);
        assertEquals(2L, dto2.getIdPromedio());
        assertEquals(200L, dto2.getIdCurso());
        assertEquals("EST-002", dto2.getIdEstudiante());
        assertEquals(6.2, dto2.getPromedioGeneral());
        assertEquals(3, dto2.getTotalEvaluaciones());

        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getAll_whenNoRecords_returnsEmptyList() {
        // Given
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<PromedioDTO> result = service.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void getById_whenFound_returnsMappedDto() {
        // Given
        Promedio entity = buildEntity(10L);
        when(repository.findById(10L)).thenReturn(Optional.of(entity));

        // When
        Optional<PromedioDTO> result = service.getById(10L);

        // Then
        assertTrue(result.isPresent());
        PromedioDTO dto = result.get();
        assertEquals(10L, dto.getIdPromedio());
        assertEquals(100L, dto.getIdCurso());
        assertEquals("EST-001", dto.getIdEstudiante());
        assertEquals(5.8, dto.getPromedioGeneral());
        assertEquals(4, dto.getTotalEvaluaciones());
        verify(repository).findById(10L);
    }

    @Test
    void getById_whenNotFound_returnsEmptyOptional() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<PromedioDTO> result = service.getById(99L);

        // Then
        assertTrue(result.isEmpty());
        assertFalse(result.isPresent());
        verify(repository).findById(99L);
    }

    @Test
    void guardar_savesEntityAndReturnsMappedDto() {
        // Given
        PromedioDTO input = new PromedioDTO(null, 300L, "EST-003", 4.9, 5);
        // Persisted entity returned by the mocked repository (order: idPromedio, idEstudiante, idCurso, ...)
        Promedio persisted = new Promedio(8L, "EST-003", 300L, 4.9, 5);
        when(repository.save(any(Promedio.class))).thenReturn(persisted);

        // When
        PromedioDTO result = service.guardar(input);

        // Then: returned DTO is convertToDTO(persisted)
        assertNotNull(result);
        assertEquals(8L, result.getIdPromedio());
        assertEquals(300L, result.getIdCurso());
        assertEquals("EST-003", result.getIdEstudiante());
        assertEquals(4.9, result.getPromedioGeneral());
        assertEquals(5, result.getTotalEvaluaciones());

        // Verify the entity built from the DTO before saving.
        // Note: convertToEntity maps DTO.idEstudiante -> entity.idEstudiante and DTO.idCurso -> entity.idCurso
        // via the Promedio(idPromedio, idEstudiante, idCurso, ...) constructor.
        ArgumentCaptor<Promedio> captor = ArgumentCaptor.forClass(Promedio.class);
        verify(repository).save(captor.capture());
        Promedio saved = captor.getValue();
        assertEquals(input.getIdPromedio(), saved.getIdPromedio());
        assertEquals("EST-003", saved.getIdEstudiante());
        assertEquals(300L, saved.getIdCurso());
        assertEquals(4.9, saved.getPromedioGeneral());
        assertEquals(5, saved.getTotalEvaluaciones());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void borrar_callsDeleteById() {
        // Given
        Long id = 7L;

        // When
        service.borrar(id);

        // Then
        verify(repository, times(1)).deleteById(id);
        verify(repository, never()).findById(any());
        verifyNoMoreInteractions(repository);
    }
}
