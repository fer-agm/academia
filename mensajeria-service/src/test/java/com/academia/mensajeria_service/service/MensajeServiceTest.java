package com.academia.mensajeria_service.service;

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

import com.academia.mensajeria_service.dto.MensajeDTO;
import com.academia.mensajeria_service.model.Mensaje;
import com.academia.mensajeria_service.repository.MensajeRepository;

import reactor.core.publisher.Mono;

/**
 * Pure Mockito unit tests for {@link MensajeService}.
 * No Spring context, no database.
 */
@ExtendWith(MockitoExtension.class)
class MensajeServiceTest {

    private static final String USUARIO_URL = "http://gateway/usuarios/run/%s/existe";

    @Mock
    private MensajeRepository repository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private MensajeService service;

    @BeforeEach
    void setUp() {
        // @Value fields are not populated outside Spring; inject format-string URLs manually.
        ReflectionTestUtils.setField(service, "usuarioExistsUrl", USUARIO_URL);
    }

    /**
     * Stubs the WebClient fluent chain. generarMensaje() validates estudiante;
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

    // Mensaje entity constructor order: (idMensaje, idEmisor, idReceptor, mensaje)
    private Mensaje buildEntity(Long id) {
        return new Mensaje(id, "312132", "13124", "Mensaje de prueba");
    }

    @Test
    void getAll_returnsMappedDtoList() {
        // Given
        Mensaje entity1 = new Mensaje(1L, "3423", "132213", "Mensaje de prueba");
        Mensaje entity2 = new Mensaje(2L, "35435", "32443", "Mensaje de prueba");
        when(repository.findAll()).thenReturn(List.of(entity1, entity2));

        // When
        List<MensajeDTO> result = service.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // convertToDTO maps: (idNotificacion, idCertificado, idEstudiante)
        MensajeDTO dto1 = result.get(0);
        assertEquals(1L, dto1.getIdMensaje());
        assertEquals("3423", dto1.getIdEmisor());
        assertEquals("132213", dto1.getIdReceptor());
        assertEquals("Mensaje de prueba", dto1.getMensaje());

        MensajeDTO dto2 = result.get(1);
        assertEquals(2L, dto2.getIdMensaje());
        assertEquals("35435", dto2.getIdEmisor());
        assertEquals("32443", dto2.getIdReceptor());
        assertEquals("Mensaje de prueba", dto2.getMensaje());

        verify(repository).findAll();
    }

    @Test
    void getAll_whenNoRecords_returnsEmptyList() {
        // Given
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<MensajeDTO> result = service.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void getById_whenFound_returnsMappedDto() {
        // Given
        Mensaje entity = buildEntity(10L);
        when(repository.findById(10L)).thenReturn(Optional.of(entity));

        // When
        Optional<MensajeDTO> result = service.getById(10L);

        // Then
        assertTrue(result.isPresent());
        MensajeDTO dto = result.get();
        assertEquals(10L, dto.getIdMensaje());
        assertEquals("312132", dto.getIdEmisor());
        assertEquals("13124", dto.getIdReceptor());
        assertEquals("Mensaje de prueba", dto.getMensaje());
        verify(repository).findById(10L);
    }

    @Test
    void getById_whenNotFound_returnsEmptyOptional() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<MensajeDTO> result = service.getById(99L);

        // Then
        assertTrue(result.isEmpty());
        assertFalse(result.isPresent());
        verify(repository).findById(99L);
    }

    @Test
    void generarMensaje_savesEntityAndReturnsMappedDto() {
        // Given: both cross-service existence checks return true
        MensajeDTO input = new MensajeDTO(null, "12345", "3243", "Mensaje de prueba");
        // Persisted entity returned by the mocked repository (order: idMensaje, idEmisor, idReceptor)
        Mensaje persisted = new Mensaje(8L, "12432", "3432", "Mensaje de prueba");
        stubExistenceChecks(Boolean.TRUE, Boolean.TRUE);
        when(repository.save(any(Mensaje.class))).thenReturn(persisted);

        // When
        MensajeDTO result = service.generarMensaje(input, "Bearer token");

        // Then: returned DTO is convertToDTO(persisted)
        assertNotNull(result);
        assertEquals(8L, result.getIdMensaje());
        assertEquals("12432", result.getIdEmisor());
        assertEquals("3432", result.getIdReceptor());
        assertEquals("Mensaje de prueba", result.getMensaje());

        // Verify the entity built from the DTO before saving.
        ArgumentCaptor<Mensaje> captor = ArgumentCaptor.forClass(Mensaje.class);
        verify(repository).save(captor.capture());
        Mensaje saved = captor.getValue();
        assertEquals(input.getIdMensaje(), saved.getIdMensaje());
        assertEquals(input.getIdEmisor(), saved.getIdEmisor());
        assertEquals(input.getIdReceptor(), saved.getIdReceptor());
    }

    @Test
    void generarMensaje_whenUserDoesNotExist_throwsIllegalArgumentAndDoesNotSave() {
        // Given: first check (User) returns false
        MensajeDTO input = new MensajeDTO(null, "idEmisor", "idReceptor", "Mensaje de prueba");
        stubExistenceChecks(Boolean.FALSE, Boolean.TRUE);

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.generarMensaje(input, "Bearer token"));
        assertEquals("El emisor con id idEmisor no existe", ex.getMessage());
        verify(repository, never()).save(any(Mensaje.class));
    }

    @Test
    void generarMensaje_whenEstudianteDoesNotExist_throwsIllegalArgumentAndDoesNotSave() {
        // Given: usuario ok (true), estudiante not found (false)
        MensajeDTO input = new MensajeDTO(null, "13221", "13221", "Mensaje de prueba");
        stubExistenceChecks(Boolean.TRUE, Boolean.FALSE);

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.generarMensaje(input, "Bearer token"));
        assertEquals("El receptor con id 13221 no existe", ex.getMessage());
        verify(repository, never()).save(any(Mensaje.class));
    }

    // @Test
    // void borrar_callsDeleteById() {
    //     // Given
    //     Long id = 7L;

    //     // When
    //     service.borrar(id);

    //     // Then
    //     verify(repository, times(1)).deleteById(id);
    //     verify(repository, never()).findById(any());
    // }
}
