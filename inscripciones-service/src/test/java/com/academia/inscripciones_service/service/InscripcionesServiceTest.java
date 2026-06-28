package com.academia.inscripciones_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.academia.inscripciones_service.model.Inscripciones;
import com.academia.inscripciones_service.repository.InscripcionesRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class InscripcionesServiceTest {

    @Mock
    private InscripcionesRepository inscripcionesRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    @InjectMocks
    private InscripcionesService inscripcionesService;

    private Inscripciones inscripcion;

    @BeforeEach
    void setUp() {
        // Given: inject the @Value field and a baseline entity
        ReflectionTestUtils.setField(inscripcionesService, "clasesServiceUrl", "http://localhost:8081");
        inscripcion = new Inscripciones(1L, "11.111.111-1", 100L, LocalDateTime.now(), "ACTIVO");
    }

    @Test
    @DisplayName("listarTodas devuelve todas las inscripciones")
    void listarTodas_devuelveLista() {
        // Given
        Inscripciones otra = new Inscripciones(2L, "22.222.222-2", 200L, LocalDateTime.now(), "ACTIVO");
        List<Inscripciones> esperadas = Arrays.asList(inscripcion, otra);
        when(inscripcionesRepository.findAll()).thenReturn(esperadas);

        // When
        List<Inscripciones> resultado = inscripcionesService.listarTodas();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(esperadas, resultado);
        verify(inscripcionesRepository).findAll();
    }

    @Test
    @DisplayName("buscarPorId devuelve la inscripcion cuando existe")
    void buscarPorId_encontrada() {
        // Given
        when(inscripcionesRepository.findById(1L)).thenReturn(Optional.of(inscripcion));

        // When
        Inscripciones resultado = inscripcionesService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertSame(inscripcion, resultado);
        verify(inscripcionesRepository).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId devuelve null cuando no existe")
    void buscarPorId_noEncontrada() {
        // Given
        when(inscripcionesRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Inscripciones resultado = inscripcionesService.buscarPorId(99L);

        // Then
        assertNull(resultado);
        verify(inscripcionesRepository).findById(99L);
    }

    @Test
    @DisplayName("crearInscripcion fija la fecha y guarda manteniendo el estado dado")
    void crearInscripcion_conEstado() {
        // Given
        Inscripciones nueva = new Inscripciones(null, "33.333.333-3", 300L, null, "PENDIENTE");
        when(inscripcionesRepository.save(any(Inscripciones.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Inscripciones resultado = inscripcionesService.crearInscripcion(nueva);

        // Then
        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
        assertNotNull(resultado.getFecha_inscripcion());
        verify(inscripcionesRepository).save(nueva);
    }

    @Test
    @DisplayName("crearInscripcion asigna estado ACTIVO por defecto cuando es null")
    void crearInscripcion_estadoPorDefecto() {
        // Given
        Inscripciones nueva = new Inscripciones(null, "44.444.444-4", 400L, null, null);
        when(inscripcionesRepository.save(any(Inscripciones.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Inscripciones resultado = inscripcionesService.crearInscripcion(nueva);

        // Then
        assertNotNull(resultado);
        assertEquals("ACTIVO", resultado.getEstado());
        assertNotNull(resultado.getFecha_inscripcion());
        verify(inscripcionesRepository).save(nueva);
    }

    @Test
    @DisplayName("listarPorEstudiante delega en el repositorio por RUN")
    void listarPorEstudiante_ok() {
        // Given
        String run = "11.111.111-1";
        List<Inscripciones> esperadas = List.of(inscripcion);
        when(inscripcionesRepository.findByIdEstudiante(run)).thenReturn(esperadas);

        // When
        List<Inscripciones> resultado = inscripcionesService.listarPorEstudiante(run);

        // Then
        assertEquals(1, resultado.size());
        assertEquals(esperadas, resultado);
        verify(inscripcionesRepository).findByIdEstudiante(run);
    }

    @Test
    @DisplayName("listarPorEstudiante devuelve lista vacia cuando no hay coincidencias")
    void listarPorEstudiante_vacia() {
        // Given
        when(inscripcionesRepository.findByIdEstudiante("99.999.999-9")).thenReturn(List.of());

        // When
        List<Inscripciones> resultado = inscripcionesService.listarPorEstudiante("99.999.999-9");

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(inscripcionesRepository).findByIdEstudiante("99.999.999-9");
    }

    @Test
    @DisplayName("actualizar modifica estado e idCurso cuando la inscripcion existe")
    void actualizar_encontrada() {
        // Given
        Inscripciones datos = new Inscripciones(null, null, 999L, null, "FINALIZADO");
        when(inscripcionesRepository.findById(1L)).thenReturn(Optional.of(inscripcion));
        when(inscripcionesRepository.save(any(Inscripciones.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Inscripciones resultado = inscripcionesService.actualizar(1L, datos);

        // Then
        assertNotNull(resultado);
        assertEquals("FINALIZADO", resultado.getEstado());
        assertEquals(999L, resultado.getIdCurso());
        verify(inscripcionesRepository).findById(1L);
        verify(inscripcionesRepository).save(inscripcion);
    }

    @Test
    @DisplayName("actualizar devuelve null y no guarda cuando no existe")
    void actualizar_noEncontrada() {
        // Given
        Inscripciones datos = new Inscripciones(null, null, 999L, null, "FINALIZADO");
        when(inscripcionesRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Inscripciones resultado = inscripcionesService.actualizar(99L, datos);

        // Then
        assertNull(resultado);
        verify(inscripcionesRepository).findById(99L);
        verify(inscripcionesRepository, never()).save(any(Inscripciones.class));
    }

    @Test
    @DisplayName("eliminar devuelve true y borra cuando la inscripcion existe")
    void eliminar_existente() {
        // Given
        when(inscripcionesRepository.existsById(1L)).thenReturn(true);

        // When
        boolean resultado = inscripcionesService.eliminar(1L);

        // Then
        assertTrue(resultado);
        verify(inscripcionesRepository).existsById(1L);
        verify(inscripcionesRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar devuelve false y no borra cuando no existe")
    void eliminar_inexistente() {
        // Given
        when(inscripcionesRepository.existsById(99L)).thenReturn(false);

        // When
        boolean resultado = inscripcionesService.eliminar(99L);

        // Then
        assertFalse(resultado);
        verify(inscripcionesRepository).existsById(99L);
        verify(inscripcionesRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("crearInscripcionConValidaciones guarda y responde cuando el curso existe")
    void crearInscripcionConValidaciones_cursoValido() {
        // Given
        Inscripciones nueva = new Inscripciones(null, "55.555.555-5", 500L, null, null);
        when(webClient.get()
                .uri(anyString())
                .retrieve()
                .bodyToMono(Void.class))
                .thenReturn(Mono.empty());
        when(inscripcionesRepository.save(any(Inscripciones.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Map<String, Object> response = inscripcionesService.crearInscripcionConValidaciones(nueva).block();

        // Then
        assertNotNull(response);
        assertEquals("Inscripción creada exitosamente", response.get("mensaje"));
        Inscripciones guardada = (Inscripciones) response.get("inscripcion");
        assertNotNull(guardada);
        assertEquals("ACTIVO", guardada.getEstado());
        assertNotNull(guardada.getFecha_inscripcion());
        verify(inscripcionesRepository).save(nueva);
    }

    @Test
    @DisplayName("crearInscripcionConValidaciones respeta el estado provisto por el cliente")
    void crearInscripcionConValidaciones_respetaEstado() {
        // Given
        Inscripciones nueva = new Inscripciones(null, "66.666.666-6", 600L, null, "PENDIENTE");
        when(webClient.get()
                .uri(anyString())
                .retrieve()
                .bodyToMono(Void.class))
                .thenReturn(Mono.empty());
        when(inscripcionesRepository.save(any(Inscripciones.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Map<String, Object> response = inscripcionesService.crearInscripcionConValidaciones(nueva).block();

        // Then
        assertNotNull(response);
        Inscripciones guardada = (Inscripciones) response.get("inscripcion");
        assertEquals("PENDIENTE", guardada.getEstado());
        verify(inscripcionesRepository).save(nueva);
    }

    @Test
    @DisplayName("crearInscripcionConValidaciones falla cuando el curso no existe y no guarda")
    void crearInscripcionConValidaciones_cursoInvalido() {
        // Given
        Inscripciones nueva = new Inscripciones(null, "77.777.777-7", 700L, null, null);
        when(webClient.get()
                .uri(anyString())
                .retrieve()
                .bodyToMono(Void.class))
                .thenReturn(Mono.error(new RuntimeException("404 Not Found")));

        // When
        Mono<Map<String, Object>> mono = inscripcionesService.crearInscripcionConValidaciones(nueva);

        // Then
        RuntimeException ex = assertThrows(RuntimeException.class, mono::block);
        assertEquals("Curso con ID 700 no encontrado", ex.getMessage());
        verify(inscripcionesRepository, never()).save(any(Inscripciones.class));
    }
}
