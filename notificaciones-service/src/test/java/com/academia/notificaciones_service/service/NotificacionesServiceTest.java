package com.academia.notificaciones_service.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.academia.notificaciones_service.dto.NotificacionesDTO;
import com.academia.notificaciones_service.model.Notificaciones;
import com.academia.notificaciones_service.repository.NotificacionesRepository;

import reactor.core.publisher.Mono;

/**
 * Pure Mockito unit tests for {@link NotificacionesService}.
 * No Spring context, no database.
 */
@ExtendWith(MockitoExtension.class)
class NotificacionesServiceTest {

    private static final String CERTIFICADO_URL = "http://gateway/certificado/%d/existe";
    private static final String USUARIO_URL = "http://gateway/usuarios/run/%s/existe";

    @Mock
    private NotificacionesRepository repository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private NotificacionesService service;

    @BeforeEach
    void setUp() {
        // @Value fields are not populated outside Spring; inject format-string URLs manually.
        ReflectionTestUtils.setField(service, "certificadoExistsUrl", CERTIFICADO_URL);
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

    // Notificaciones entity constructor order: (idNotificacion, idEstudiante, idCertificado)
    private Notificaciones buildEntity(Long id) {
        return new Notificaciones(id, "EST-001", 100L);
    }

    @Test
    void getAll_returnsMappedDtoList() {
        // Given
        Notificaciones entity1 = new Notificaciones(1L, "EST-001", 100L);
        Notificaciones entity2 = new Notificaciones(2L, "EST-002", 200L);
        when(repository.findAll()).thenReturn(List.of(entity1, entity2));

        // When
        List<NotificacionesDTO> result = service.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // convertToDTO maps: (idNotificacion, idCertificado, idEstudiante)
        NotificacionesDTO dto1 = result.get(0);
        assertEquals(1L, dto1.getIdNotificacion());
        assertEquals(100L, dto1.getIdCertificado());
        assertEquals("EST-001", dto1.getIdEstudiante());

        NotificacionesDTO dto2 = result.get(1);
        assertEquals(2L, dto2.getIdNotificacion());
        assertEquals(200L, dto2.getIdCertificado());
        assertEquals("EST-002", dto2.getIdEstudiante());

        verify(repository).findAll();
    }

    @Test
    void getAll_whenNoRecords_returnsEmptyList() {
        // Given
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<NotificacionesDTO> result = service.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void getById_whenFound_returnsMappedDto() {
        // Given
        Notificaciones entity = buildEntity(10L);
        when(repository.findById(10L)).thenReturn(Optional.of(entity));

        // When
        Optional<NotificacionesDTO> result = service.getById(10L);

        // Then
        assertTrue(result.isPresent());
        NotificacionesDTO dto = result.get();
        assertEquals(10L, dto.getIdNotificacion());
        assertEquals(100L, dto.getIdCertificado());
        assertEquals("EST-001", dto.getIdEstudiante());
        verify(repository).findById(10L);
    }

    @Test
    void getById_whenNotFound_returnsEmptyOptional() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<NotificacionesDTO> result = service.getById(99L);

        // Then
        assertTrue(result.isEmpty());
        assertFalse(result.isPresent());
        verify(repository).findById(99L);
    }

    @Test
    void guardar_savesEntityAndReturnsMappedDto() {
        // Given: both cross-service existence checks return true
        NotificacionesDTO input = new NotificacionesDTO(null, "EST-003", 300L);
        // Persisted entity returned by the mocked repository (order: idNotificacion, idEstudiante, idCertificado)
        Notificaciones persisted = new Notificaciones(8L, "EST-003", 300L);
        stubExistenceChecks(Boolean.TRUE, Boolean.TRUE);
        when(repository.save(any(Notificaciones.class))).thenReturn(persisted);

        // When
        NotificacionesDTO result = service.guardar(input, "Bearer token");

        // Then: returned DTO is convertToDTO(persisted)
        assertNotNull(result);
        assertEquals(8L, result.getIdNotificacion());
        assertEquals(300L, result.getIdCertificado());
        assertEquals("EST-003", result.getIdEstudiante());

        // Verify the entity built from the DTO before saving.
        ArgumentCaptor<Notificaciones> captor = ArgumentCaptor.forClass(Notificaciones.class);
        verify(repository).save(captor.capture());
        Notificaciones saved = captor.getValue();
        assertEquals(input.getIdNotificacion(), saved.getIdNotificacion());
        assertEquals("EST-003", saved.getIdEstudiante());
        assertEquals(300L, saved.getIdCertificado());
    }

    @Test
    void guardar_whenCursoDoesNotExist_throwsIllegalArgumentAndDoesNotSave() {
        // Given: first check (curso) returns false
        NotificacionesDTO input = new NotificacionesDTO(null, "EST-003", 300L);
        stubExistenceChecks(Boolean.FALSE, Boolean.TRUE);

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.guardar(input, "Bearer token"));
        assertEquals("El curso con id 300 no existe", ex.getMessage());
        verify(repository, never()).save(any(Notificaciones.class));
    }

    @Test
    void guardar_whenEstudianteDoesNotExist_throwsIllegalArgumentAndDoesNotSave() {
        // Given: curso ok (true), estudiante not found (false)
        NotificacionesDTO input = new NotificacionesDTO(null, "EST-003", 300L);
        stubExistenceChecks(Boolean.TRUE, Boolean.FALSE);

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.guardar(input, "Bearer token"));
        assertEquals("El estudiante con RUN EST-003 no existe", ex.getMessage());
        verify(repository, never()).save(any(Notificaciones.class));
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
