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
        ReflectionTestUtils.setField(service, "usuarioExistsUrl", USUARIO_URL);
    }

    /**
     * Stubs the WebClient fluent chain. guardar() validates evaluacion first, then estudiante;
     * successive block() invocations return the provided answers in that order.
     */
    @SuppressWarnings("unchecked")
    private void stubExistenceChecks(Boolean primero, Boolean segundo) {
        Mono<Boolean> mono = mock(Mono.class);
        lenient().doReturn(requestHeadersUriSpec).when(webClient).get();
        lenient().doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        lenient().doReturn(requestHeadersSpec).when(requestHeadersSpec).headers(any());
        lenient().doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        lenient().doReturn(mono).when(responseSpec).bodyToMono(Boolean.class);
        lenient().doReturn(primero, segundo).when(mono).block();
    }

    private Calificacion buildEntity(Long id) {
        return new Calificacion(id, "EST-001", LocalDate.of(2026, 1, 15), 6.5);
    }

    @Test
    void getAll_returnsMappedDtoList() {
        // Given
        Calificacion entity1 = new Calificacion(1L, "EST-001", LocalDate.of(2026, 1, 15), 6.5);
        Calificacion entity2 = new Calificacion(2L, "EST-002", LocalDate.of(2026, 2, 20), 4.0);
        when(repository.findAll()).thenReturn(List.of(entity1, entity2));

        // When
        List<CalificacionDTO> result = service.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        CalificacionDTO dto1 = result.get(0);
        assertEquals(1L, dto1.getIdEvaluacion());
        assertEquals("EST-001", dto1.getIdEstudiante());
        assertEquals(LocalDate.of(2026, 1, 15), dto1.getFecha());
        assertEquals(6.5, dto1.getNota());

        CalificacionDTO dto2 = result.get(1);
        assertEquals(2L, dto2.getIdEvaluacion());
        assertEquals("EST-002", dto2.getIdEstudiante());
        assertEquals(LocalDate.of(2026, 2, 20), dto2.getFecha());
        assertEquals(4.0, dto2.getNota());

        verify(repository).findAll();
    }

    @Test
    void getAll_whenNoRecords_returnsEmptyList() {
        // Given
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<CalificacionDTO> result = service.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void getById_whenFound_returnsMappedDto() {
        // Given
        Calificacion entity = buildEntity(10L);
        when(repository.findById(10L)).thenReturn(Optional.of(entity));

        // When
        Optional<CalificacionDTO> result = service.getById(10L);

        // Then
        assertTrue(result.isPresent());
        CalificacionDTO dto = result.get();
        assertEquals(10L, dto.getIdEvaluacion());
        assertEquals("EST-001", dto.getIdEstudiante());
        assertEquals(LocalDate.of(2026, 1, 15), dto.getFecha());
        assertEquals(6.5, dto.getNota());
        verify(repository).findById(10L);
    }

    @Test
    void getById_whenNotFound_returnsEmptyOptional() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<CalificacionDTO> result = service.getById(99L);

        // Then
        assertTrue(result.isEmpty());
        assertFalse(result.isPresent());
        verify(repository).findById(99L);
    }

    @Test
    void guardar_savesEntityAndReturnsMappedDto() {
        // Given: both cross-service existence checks return true
        CalificacionDTO input = new CalificacionDTO(null, "EST-003", LocalDate.of(2026, 3, 10), 5.5);
        Calificacion persisted = new Calificacion(5L, "EST-003", LocalDate.of(2026, 3, 10), 5.5);
        stubExistenceChecks(Boolean.TRUE, Boolean.TRUE);
        when(repository.save(any(Calificacion.class))).thenReturn(persisted);

        // When
        CalificacionDTO result = service.guardar(input, "Bearer token");

        // Then
        assertNotNull(result);
        assertEquals(5L, result.getIdEvaluacion());
        assertEquals("EST-003", result.getIdEstudiante());
        assertEquals(LocalDate.of(2026, 3, 10), result.getFecha());
        assertEquals(5.5, result.getNota());

        // Verify the entity passed to the repository was mapped from the DTO
        ArgumentCaptor<Calificacion> captor = ArgumentCaptor.forClass(Calificacion.class);
        verify(repository).save(captor.capture());
        Calificacion saved = captor.getValue();
        assertEquals(input.getIdEvaluacion(), saved.getIdEvaluacion());
        assertEquals("EST-003", saved.getIdEstudiante());
        assertEquals(LocalDate.of(2026, 3, 10), saved.getFecha());
        assertEquals(5.5, saved.getNota());
    }

    @Test
    void guardar_whenEstudianteDoesNotExist_throwsIllegalArgumentAndDoesNotSave() {
        // Given: estudiante not found (the only cross-service reference checked)
        CalificacionDTO input = new CalificacionDTO(7L, "EST-003", LocalDate.of(2026, 3, 10), 5.5);
        stubExistenceChecks(Boolean.FALSE, Boolean.FALSE);

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.guardar(input, "Bearer token"));
        assertEquals("El estudiante con RUN EST-003 no existe", ex.getMessage());
        verify(repository, never()).save(any(Calificacion.class));
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
    }
}
