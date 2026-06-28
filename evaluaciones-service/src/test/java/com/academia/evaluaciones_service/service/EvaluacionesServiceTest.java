package com.academia.evaluaciones_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import com.academia.evaluaciones_service.model.Evaluaciones;
import com.academia.evaluaciones_service.repository.EvaluacionesRepository;

@ExtendWith(MockitoExtension.class)
class EvaluacionesServiceTest {

    private static final String CURSO_URL = "http://gateway/cursos/%d/existe";

    @Mock
    private EvaluacionesRepository evaluacionesRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private EvaluacionesService evaluacionesService;

    @BeforeEach
    void setUp() {
        // @Value("${api.curso.exists}") is not populated outside Spring; inject manually.
        ReflectionTestUtils.setField(evaluacionesService, "cursoExistsUrl", CURSO_URL);
    }

    private Evaluaciones buildEvaluacion() {
        return new Evaluaciones(1L, 100L, 40, 70);
    }

    /** Stubs the WebClient fluent chain used by guardar() to validate the course. */
    @SuppressWarnings("unchecked")
    private void stubCursoExists(Boolean answer) {
        Mono<Boolean> mono = mock(Mono.class);
        lenient().doReturn(requestHeadersUriSpec).when(webClient).get();
        lenient().doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        lenient().doReturn(requestHeadersSpec).when(requestHeadersSpec).headers(any());
        lenient().doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        lenient().doReturn(mono).when(responseSpec).bodyToMono(Boolean.class);
        lenient().doReturn(answer).when(mono).block();
    }

    @Test
    void getAllEvaluaciones_returnsListFromRepository() {
        // Given
        Evaluaciones e1 = buildEvaluacion();
        Evaluaciones e2 = new Evaluaciones(2L, 200L, 50, 90);
        when(evaluacionesRepository.findAll()).thenReturn(Arrays.asList(e1, e2));

        // When
        List<Evaluaciones> result = evaluacionesService.getAllEvaluaciones();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100L, result.get(0).getIdCurso());
        verify(evaluacionesRepository, times(1)).findAll();
    }

    @Test
    void getAllEvaluaciones_returnsEmptyListWhenNoEvaluaciones() {
        // Given
        when(evaluacionesRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Evaluaciones> result = evaluacionesService.getAllEvaluaciones();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(evaluacionesRepository, times(1)).findAll();
    }

    @Test
    void getEvaluacionById_returnsEvaluacionWhenFound() {
        // Given
        Evaluaciones evaluacion = buildEvaluacion();
        when(evaluacionesRepository.findById(1L)).thenReturn(Optional.of(evaluacion));

        // When
        Optional<Evaluaciones> result = evaluacionesService.getEvaluacionById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getIdEvaluacion());
        assertEquals(40, result.get().getPuntMin());
        assertEquals(70, result.get().getPuntMax());
        verify(evaluacionesRepository, times(1)).findById(1L);
    }

    @Test
    void getEvaluacionById_returnsEmptyWhenNotFound() {
        // Given
        when(evaluacionesRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Evaluaciones> result = evaluacionesService.getEvaluacionById(99L);

        // Then
        assertFalse(result.isPresent());
        verify(evaluacionesRepository, times(1)).findById(99L);
    }

    @Test
    void existe_returnsTrueWhenRepositoryConfirms() {
        // Given
        when(evaluacionesRepository.existsById(1L)).thenReturn(true);

        // When / Then
        assertTrue(evaluacionesService.existe(1L));
        verify(evaluacionesRepository, times(1)).existsById(1L);
    }

    @Test
    void existe_returnsFalseWhenRepositoryDenies() {
        // Given
        when(evaluacionesRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertFalse(evaluacionesService.existe(99L));
        verify(evaluacionesRepository, times(1)).existsById(99L);
    }

    @Test
    void guardar_cursoExiste_savesAndReturnsEvaluacion() {
        // Given
        Evaluaciones toSave = new Evaluaciones(null, 300L, 30, 60);
        Evaluaciones saved = new Evaluaciones(1L, 300L, 30, 60);
        stubCursoExists(Boolean.TRUE);
        when(evaluacionesRepository.save(toSave)).thenReturn(saved);

        // When
        Evaluaciones result = evaluacionesService.guardar(toSave, "Bearer token");

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getIdEvaluacion());
        assertEquals(300L, result.getIdCurso());
        verify(evaluacionesRepository, times(1)).save(toSave);
    }

    @Test
    void guardar_cursoExiste_returnsSameInstanceFromRepository() {
        // Given
        Evaluaciones evaluacion = buildEvaluacion();
        stubCursoExists(Boolean.TRUE);
        when(evaluacionesRepository.save(any(Evaluaciones.class))).thenReturn(evaluacion);

        // When
        Evaluaciones result = evaluacionesService.guardar(evaluacion, "Bearer token");

        // Then
        assertSame(evaluacion, result);
        verify(evaluacionesRepository, times(1)).save(evaluacion);
    }

    @Test
    void guardar_cursoNoExiste_lanzaIllegalArgumentYNoGuarda() {
        // Given
        Evaluaciones evaluacion = buildEvaluacion();
        stubCursoExists(Boolean.FALSE);

        // When / Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> evaluacionesService.guardar(evaluacion, "Bearer token"));
        assertEquals("El curso con id 100 no existe", ex.getMessage());
        verify(evaluacionesRepository, never()).save(any(Evaluaciones.class));
    }

    @Test
    void guardar_validacionNull_lanzaIllegalStateYNoGuarda() {
        // Given
        Evaluaciones evaluacion = buildEvaluacion();
        stubCursoExists(null);

        // When / Then
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> evaluacionesService.guardar(evaluacion, "Bearer token"));
        assertEquals("No se pudo validar el curso con id 100", ex.getMessage());
        verify(evaluacionesRepository, never()).save(any(Evaluaciones.class));
    }

    @Test
    void borrar_deletesByIdAndReturnsVoid() {
        // Given
        Long id = 1L;

        // When
        evaluacionesService.borrar(id);

        // Then
        verify(evaluacionesRepository, times(1)).deleteById(id);
        verify(evaluacionesRepository, never()).findById(any());
    }
}
