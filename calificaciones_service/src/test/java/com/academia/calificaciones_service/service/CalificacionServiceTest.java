package com.academia.calificaciones_service.service;

import com.academia.calificaciones_service.dto.CalificacionDTO;
import com.academia.calificaciones_service.model.Calificacion;
import com.academia.calificaciones_service.repository.CalificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pure Mockito unit tests for {@link CalificacionService}.
 * No Spring context, no database.
 */
@ExtendWith(MockitoExtension.class)
class CalificacionServiceTest {

    private static final String EVALUACION_URL = "http://gateway/evaluaciones/%d/existe";
    private static final String USUARIO_URL = "http://gateway/usuarios/run/%s/existe";

    @Mock
    private CalificacionRepository repository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CalificacionService service;

    @BeforeEach
    void setUp() {
        // @Value fields are not populated outside Spring; inject format-string URLs manually.
        ReflectionTestUtils.setField(service, "evaluacionExistsUrl", EVALUACION_URL);
        ReflectionTestUtils.setField(service, "usuarioExistsUrl", USUARIO_URL);
    }

    /**
     * Stubs the WebClient fluent chain. guardar() valida la evaluación primero y el estudiante después;
     * las llamadas sucesivas a block() devuelven los valores dados en ese orden.
     */
    @SuppressWarnings("unchecked")
    private void stubExistenceChecks(Boolean evaluacion, Boolean estudiante) {
        Mono<Boolean> mono = mock(Mono.class);
        lenient().doReturn(requestHeadersUriSpec).when(webClient).get();
        lenient().doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        lenient().doReturn(requestHeadersSpec).when(requestHeadersSpec).headers(any());
        lenient().doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        lenient().doReturn(mono).when(responseSpec).bodyToMono(Boolean.class);
        lenient().doReturn(evaluacion, estudiante).when(mono).block();
    }

    // Calificacion(idCalificacion, idEvaluacion, idEstudiante, fecha, nota)
    private Calificacion buildEntity(Long idCalificacion) {
        return new Calificacion(idCalificacion, 10L, "EST-001", LocalDate.of(2026, 1, 15), 6.5);
    }

    @Test
    void getAll_returnsMappedDtoList() {
        // Given
        Calificacion entity1 = new Calificacion(1L, 10L, "EST-001", LocalDate.of(2026, 1, 15), 6.5);
        Calificacion entity2 = new Calificacion(2L, 20L, "EST-002", LocalDate.of(2026, 2, 20), 4.0);
        when(repository.findAll()).thenReturn(List.of(entity1, entity2));

        // When
        List<CalificacionDTO> result = service.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        CalificacionDTO dto1 = result.get(0);
        assertEquals(1L, dto1.getIdCalificacion());
        assertEquals(10L, dto1.getIdEvaluacion());
        assertEquals("EST-001", dto1.getIdEstudiante());
        assertEquals(6.5, dto1.getNota());

        CalificacionDTO dto2 = result.get(1);
        assertEquals(2L, dto2.getIdCalificacion());
        assertEquals(20L, dto2.getIdEvaluacion());

        verify(repository).findAll();
    }

    @Test
    void getAll_whenNoRecords_returnsEmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<CalificacionDTO> result = service.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void getById_whenFound_returnsMappedDto() {
        // Given
        Calificacion entity = buildEntity(5L);
        when(repository.findById(5L)).thenReturn(Optional.of(entity));

        // When
        Optional<CalificacionDTO> result = service.getById(5L);

        // Then
        assertTrue(result.isPresent());
        CalificacionDTO dto = result.get();
        assertEquals(5L, dto.getIdCalificacion());
        assertEquals(10L, dto.getIdEvaluacion());
        assertEquals("EST-001", dto.getIdEstudiante());
        verify(repository).findById(5L);
    }

    @Test
    void getById_whenNotFound_returnsEmptyOptional() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<CalificacionDTO> result = service.getById(99L);

        assertTrue(result.isEmpty());
        assertFalse(result.isPresent());
        verify(repository).findById(99L);
    }

    @Test
    void guardar_savesEntityAndReturnsMappedDto() {
        // Given: evaluación y estudiante existen
        CalificacionDTO input = new CalificacionDTO(null, 10L, "EST-003", LocalDate.of(2026, 3, 10), 5.5);
        Calificacion persisted = new Calificacion(5L, 10L, "EST-003", LocalDate.of(2026, 3, 10), 5.5);
        stubExistenceChecks(Boolean.TRUE, Boolean.TRUE);
        when(repository.save(any(Calificacion.class))).thenReturn(persisted);

        // When
        CalificacionDTO result = service.guardar(input, "Bearer token");

        // Then
        assertNotNull(result);
        assertEquals(5L, result.getIdCalificacion());
        assertEquals(10L, result.getIdEvaluacion());
        assertEquals("EST-003", result.getIdEstudiante());
        assertEquals(5.5, result.getNota());

        ArgumentCaptor<Calificacion> captor = ArgumentCaptor.forClass(Calificacion.class);
        verify(repository).save(captor.capture());
        Calificacion saved = captor.getValue();
        assertEquals(10L, saved.getIdEvaluacion());
        assertEquals("EST-003", saved.getIdEstudiante());
    }

    @Test
    void guardar_whenEvaluacionDoesNotExist_throwsAndDoesNotSave() {
        // Given: la evaluación NO existe (primer chequeo en false)
        CalificacionDTO input = new CalificacionDTO(null, 99L, "EST-003", LocalDate.of(2026, 3, 10), 5.5);
        stubExistenceChecks(Boolean.FALSE, Boolean.TRUE);

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.guardar(input, "Bearer token"));
        assertEquals("La evaluación con id 99 no existe", ex.getMessage());
        verify(repository, never()).save(any(Calificacion.class));
    }

    @Test
    void guardar_whenEstudianteDoesNotExist_throwsAndDoesNotSave() {
        // Given: evaluación ok, estudiante NO existe
        CalificacionDTO input = new CalificacionDTO(null, 10L, "EST-003", LocalDate.of(2026, 3, 10), 5.5);
        stubExistenceChecks(Boolean.TRUE, Boolean.FALSE);

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.guardar(input, "Bearer token"));
        assertEquals("El estudiante con RUN EST-003 no existe", ex.getMessage());
        verify(repository, never()).save(any(Calificacion.class));
    }

    @Test
    void borrar_callsDeleteById() {
        Long id = 7L;

        service.borrar(id);

        verify(repository, times(1)).deleteById(id);
        verify(repository, never()).findById(any());
    }
}
