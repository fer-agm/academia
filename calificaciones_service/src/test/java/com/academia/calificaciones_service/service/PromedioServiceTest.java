package com.academia.calificaciones_service.service;

import com.academia.calificaciones_service.dto.PromedioDTO;
import com.academia.calificaciones_service.model.Promedio;
import com.academia.calificaciones_service.repository.PromedioRepository;
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
 * Pure Mockito unit tests for {@link PromedioService}.
 * No Spring context, no database.
 */
@ExtendWith(MockitoExtension.class)
class PromedioServiceTest {

    private static final String CURSO_URL = "http://gateway/cursos/%d/existe";
    private static final String USUARIO_URL = "http://gateway/usuarios/run/%s/existe";

    @Mock
    private PromedioRepository repository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private PromedioService service;

    @BeforeEach
    void setUp() {
        // @Value fields are not populated outside Spring; inject format-string URLs manually.
        ReflectionTestUtils.setField(service, "cursoExistsUrl", CURSO_URL);
        ReflectionTestUtils.setField(service, "usuarioExistsUrl", USUARIO_URL);
    }

    /**
     * Stubs the WebClient fluent chain. guardar() validates curso first, then estudiante;
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
        // Given: both cross-service existence checks return true
        PromedioDTO input = new PromedioDTO(null, 300L, "EST-003", 4.9, 5);
        // Persisted entity returned by the mocked repository (order: idPromedio, idEstudiante, idCurso, ...)
        Promedio persisted = new Promedio(8L, "EST-003", 300L, 4.9, 5);
        stubExistenceChecks(Boolean.TRUE, Boolean.TRUE);
        when(repository.save(any(Promedio.class))).thenReturn(persisted);

        // When
        PromedioDTO result = service.guardar(input, "Bearer token");

        // Then: returned DTO is convertToDTO(persisted)
        assertNotNull(result);
        assertEquals(8L, result.getIdPromedio());
        assertEquals(300L, result.getIdCurso());
        assertEquals("EST-003", result.getIdEstudiante());
        assertEquals(4.9, result.getPromedioGeneral());
        assertEquals(5, result.getTotalEvaluaciones());

        // Verify the entity built from the DTO before saving.
        ArgumentCaptor<Promedio> captor = ArgumentCaptor.forClass(Promedio.class);
        verify(repository).save(captor.capture());
        Promedio saved = captor.getValue();
        assertEquals(input.getIdPromedio(), saved.getIdPromedio());
        assertEquals("EST-003", saved.getIdEstudiante());
        assertEquals(300L, saved.getIdCurso());
        assertEquals(4.9, saved.getPromedioGeneral());
        assertEquals(5, saved.getTotalEvaluaciones());
    }

    @Test
    void guardar_whenCursoDoesNotExist_throwsIllegalArgumentAndDoesNotSave() {
        // Given: first check (curso) returns false
        PromedioDTO input = new PromedioDTO(null, 300L, "EST-003", 4.9, 5);
        stubExistenceChecks(Boolean.FALSE, Boolean.TRUE);

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.guardar(input, "Bearer token"));
        assertEquals("El curso con id 300 no existe", ex.getMessage());
        verify(repository, never()).save(any(Promedio.class));
    }

    @Test
    void guardar_whenEstudianteDoesNotExist_throwsIllegalArgumentAndDoesNotSave() {
        // Given: curso ok (true), estudiante not found (false)
        PromedioDTO input = new PromedioDTO(null, 300L, "EST-003", 4.9, 5);
        stubExistenceChecks(Boolean.TRUE, Boolean.FALSE);

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.guardar(input, "Bearer token"));
        assertEquals("El estudiante con RUN EST-003 no existe", ex.getMessage());
        verify(repository, never()).save(any(Promedio.class));
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
