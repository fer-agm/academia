package com.academia.pago_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import com.academia.pago_service.exception.BadRequestException;
import com.academia.pago_service.exception.ResourceNotFoundException;
import com.academia.pago_service.model.Pago;
import com.academia.pago_service.repository.PagoRepository;

/**
 * Pure Mockito unit tests for {@link PagoService}.
 * No Spring context, no database, no @SpringBootTest.
 */
@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    private static final String CURSO_URL = "http://gateway/cursos/%d/exists";

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private PagoService pagoService;

    private static final String USUARIO_URL = "http://gateway/usuarios/run/%s/existe";

    @BeforeEach
    void setUp() {
        // The @Value(...) fields are not populated outside Spring, so we inject
        // format-string URLs manually for String.format(...).
        ReflectionTestUtils.setField(pagoService, "cursoPath", CURSO_URL);
        ReflectionTestUtils.setField(pagoService, "usuarioExistsUrl", USUARIO_URL);
    }

    /** Builds a valid Pago with neto/descuento configured by the caller. */
    private Pago nuevoPago(int valorNeto, int descuento) {
        Pago pago = new Pago();
        pago.setId_pago(1L);
        pago.setRunEstudiante("11111111-1");
        pago.setIdCurso(10L);
        pago.setValorNeto(valorNeto);
        pago.setDescuento(descuento);
        pago.setMedioPago("TARJETA_CREDITO");
        return pago;
    }

    /**
     * Stubs the WebClient fluent chain used by the service to validate the course.
     * Covers both the guardar() chain (.uri().headers().retrieve()) and the
     * actualizar() chain (.uri().retrieve()). The terminal block() returns {@code answer}.
     */
    @SuppressWarnings("unchecked")
    private void stubCursoExists(Boolean answer) {
        Mono<Boolean> mono = mock(Mono.class);
        // lenient() because the optional .headers(...) step is only exercised by guardar(),
        // not by actualizar(); strict stubbing would otherwise flag it as unnecessary.
        lenient().doReturn(requestHeadersUriSpec).when(webClient).get();
        lenient().doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        // guardar() adds a .headers(...) step that returns the same spec
        lenient().doReturn(requestHeadersSpec).when(requestHeadersSpec).headers(any());
        lenient().doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        lenient().doReturn(mono).when(responseSpec).bodyToMono(Boolean.class);
        lenient().doReturn(answer).when(mono).block();
    }

    /**
     * Stubs the WebClient chain returning distinct answers for the curso call vs.
     * the estudiante call, selected by the formatted URI. guardar() makes the curso
     * call first (URL contains "/cursos/") then the estudiante call ("/usuarios/").
     */
    @SuppressWarnings("unchecked")
    private void stubCursoYEstudiante(Boolean cursoAnswer, Boolean estudianteAnswer) {
        Mono<Boolean> mono = mock(Mono.class);
        lenient().doReturn(requestHeadersUriSpec).when(webClient).get();
        lenient().doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        lenient().doReturn(requestHeadersSpec).when(requestHeadersSpec).headers(any());
        lenient().doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        lenient().doReturn(mono).when(responseSpec).bodyToMono(Boolean.class);
        // guardar() calls curso first, then estudiante: return the answers in that order
        // on successive block() invocations.
        lenient().doReturn(cursoAnswer, estudianteAnswer).when(mono).block();
    }

    // ---------------------------------------------------------------------
    // guardar(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("guardar: curso existe -> guarda pago y calcula IVA/total correctamente")
    void guardar_cursoExiste_guardaYCalculaTotales() {
        // Given
        Pago pago = nuevoPago(100000, 0); // sin descuento
        stubCursoExists(Boolean.TRUE);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Pago resultado = pagoService.guardar(pago, "Bearer token");

        // Then
        // subtotal = 100000 - (100000*0/100) = 100000
        // iva = 100000 * 19 / 100 = 19000
        // total = 100000 + 19000 = 119000
        assertNotNull(resultado);
        assertEquals(19000, resultado.getIva());
        assertEquals(119000, resultado.getTotalPagar());
        assertNotNull(resultado.getFecha(), "Debe asignar fecha cuando es null");
        verify(pagoRepository, times(1)).save(pago);
    }

    @Test
    @DisplayName("guardar: con descuento -> IVA/total calculados sobre el subtotal con descuento")
    void guardar_conDescuento_calculaSobreSubtotal() {
        // Given
        Pago pago = nuevoPago(100000, 10); // 10% descuento
        stubCursoExists(Boolean.TRUE);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Pago resultado = pagoService.guardar(pago, "Bearer token");

        // Then
        // subtotal = 100000 - (100000*10/100) = 90000
        // iva = 90000 * 19 / 100 = 17100
        // total = 90000 + 17100 = 107100
        assertEquals(17100, resultado.getIva());
        assertEquals(107100, resultado.getTotalPagar());
        verify(pagoRepository).save(pago);
    }

    @Test
    @DisplayName("guardar: respeta fecha existente y no la sobrescribe")
    void guardar_conFecha_noSobrescribe() {
        // Given
        Pago pago = nuevoPago(100000, 0);
        LocalDateTime fecha = LocalDateTime.of(2020, 1, 1, 12, 0);
        pago.setFecha(fecha);
        stubCursoExists(Boolean.TRUE);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Pago resultado = pagoService.guardar(pago, "Bearer token");

        // Then
        assertEquals(fecha, resultado.getFecha());
        verify(pagoRepository).save(pago);
    }

    @Test
    @DisplayName("guardar: curso NO existe (false) -> lanza ResourceNotFoundException y no guarda")
    void guardar_cursoNoExiste_lanzaResourceNotFound() {
        // Given
        Pago pago = nuevoPago(100000, 0);
        stubCursoExists(Boolean.FALSE);

        // When / Then
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> pagoService.guardar(pago, "Bearer token"));
        assertEquals("Curso no existe", ex.getMessage());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("guardar: curso existe pero estudiante NO existe -> lanza ResourceNotFoundException y no guarda")
    void guardar_estudianteNoExiste_lanzaResourceNotFound() {
        // Given: curso ok (true), estudiante no existe (false)
        Pago pago = nuevoPago(100000, 0);
        stubCursoYEstudiante(Boolean.TRUE, Boolean.FALSE);

        // When / Then
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> pagoService.guardar(pago, "Bearer token"));
        assertEquals("El estudiante con RUN " + pago.getRunEstudiante() + " no existe", ex.getMessage());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("guardar: validación devuelve null -> lanza BadRequestException y no guarda")
    void guardar_validacionNull_lanzaBadRequest() {
        // Given
        Pago pago = nuevoPago(100000, 0);
        stubCursoExists(null);

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pagoService.guardar(pago, "Bearer token"));
        assertEquals("No se pudo validar la existencia del curso", ex.getMessage());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("guardar: medioPago en blanco -> lanza IllegalArgumentException sin llamar al gateway")
    void guardar_medioPagoBlank_lanzaIllegalArgument() {
        // Given
        Pago pago = nuevoPago(100000, 0);
        pago.setMedioPago("   ");

        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> pagoService.guardar(pago, "Bearer token"));
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("guardar: totalPagar <= 0 (neto 0) -> lanza IllegalArgumentException")
    void guardar_totalCero_lanzaIllegalArgument() {
        // Given
        Pago pago = nuevoPago(0, 0); // subtotal 0 -> iva 0 -> total 0

        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> pagoService.guardar(pago, "Bearer token"));
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    // ---------------------------------------------------------------------
    // listar()
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("listar: devuelve la lista del repositorio")
    void listar_devuelveLista() {
        // Given
        List<Pago> pagos = Arrays.asList(nuevoPago(1000, 0), nuevoPago(2000, 0));
        when(pagoRepository.findAll()).thenReturn(pagos);

        // When
        List<Pago> resultado = pagoService.listar();

        // Then
        assertEquals(2, resultado.size());
        verify(pagoRepository).findAll();
    }

    // ---------------------------------------------------------------------
    // obtenerPorId(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("obtenerPorId: existe -> devuelve el pago")
    void obtenerPorId_existe_devuelvePago() {
        // Given
        Pago pago = nuevoPago(100000, 0);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        // When
        Pago resultado = pagoService.obtenerPorId(1L);

        // Then
        assertSame(pago, resultado);
        verify(pagoRepository).findById(1L);
    }

    @Test
    @DisplayName("obtenerPorId: no existe -> lanza ResourceNotFoundException")
    void obtenerPorId_noExiste_lanzaResourceNotFound() {
        // Given
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> pagoService.obtenerPorId(99L));
        assertEquals("Pago no existe", ex.getMessage());
    }

    // ---------------------------------------------------------------------
    // actualizar(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("actualizar: existe y validación ok -> recalcula IVA/total y guarda")
    void actualizar_existe_recalculaYGuarda() {
        // Given
        Pago existente = nuevoPago(50000, 0);
        Pago entrada = nuevoPago(100000, 10);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(existente));
        stubCursoExists(Boolean.TRUE);
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Pago resultado = pagoService.actualizar(1L, entrada);

        // Then
        // subtotal = 100000 - 10% = 90000 ; iva = 17100 ; total = 107100
        assertEquals(100000, resultado.getValorNeto());
        assertEquals(17100, resultado.getIva());
        assertEquals(107100, resultado.getTotalPagar());
        verify(pagoRepository).save(existente);
    }

    @Test
    @DisplayName("actualizar: pago no existe -> lanza ResourceNotFoundException")
    void actualizar_noExiste_lanzaResourceNotFound() {
        // Given
        Pago entrada = nuevoPago(100000, 0);
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> pagoService.actualizar(99L, entrada));
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("actualizar: valorNeto <= 0 -> lanza IllegalArgumentException")
    void actualizar_valorNetoInvalido_lanzaIllegalArgument() {
        // Given
        Pago existente = nuevoPago(50000, 0);
        Pago entrada = nuevoPago(0, 0);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(existente));

        // When / Then
        assertThrows(IllegalArgumentException.class,
                () -> pagoService.actualizar(1L, entrada));
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("actualizar: validación devuelve null -> lanza BadRequestException")
    void actualizar_validacionNull_lanzaBadRequest() {
        // Given
        Pago existente = nuevoPago(50000, 0);
        Pago entrada = nuevoPago(100000, 0);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(existente));
        // actualizar() uses the chain WITHOUT .headers(...); the helper stubs both forms
        stubCursoExists(null);

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pagoService.actualizar(1L, entrada));
        assertEquals("No se pudo validar la existencia del pago.", ex.getMessage());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    // ---------------------------------------------------------------------
    // eliminar(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("eliminar: existe -> borra por id")
    void eliminar_existe_borra() {
        // Given
        when(pagoRepository.existsById(1L)).thenReturn(true);

        // When
        pagoService.eliminar(1L);

        // Then
        verify(pagoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar: no existe -> lanza ResourceNotFoundException y no borra")
    void eliminar_noExiste_lanzaResourceNotFound() {
        // Given
        when(pagoRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> pagoService.eliminar(99L));
        verify(pagoRepository, never()).deleteById(any());
    }

    // ---------------------------------------------------------------------
    // calcularSubtotal(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("calcularSubtotal: aplica el porcentaje de descuento")
    void calcularSubtotal_aplicaDescuento() {
        // Given
        int neto = 100000;
        int descuento = 25;

        // When
        int subtotal = pagoService.calcularSubtotal(neto, descuento);

        // Then
        assertEquals(75000, subtotal);
    }

    @Test
    @DisplayName("calcularSubtotal: sin descuento -> devuelve neto")
    void calcularSubtotal_sinDescuento() {
        // Given / When
        int subtotal = pagoService.calcularSubtotal(100000, 0);

        // Then
        assertEquals(100000, subtotal);
    }

    @Test
    @DisplayName("calcularSubtotal: neto negativo -> lanza IllegalArgumentException")
    void calcularSubtotal_netoNegativo_lanza() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class,
                () -> pagoService.calcularSubtotal(-1, 0));
    }

    @Test
    @DisplayName("calcularSubtotal: descuento fuera de rango -> lanza IllegalArgumentException")
    void calcularSubtotal_descuentoFueraDeRango_lanza() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class,
                () -> pagoService.calcularSubtotal(100, 101));
        assertThrows(IllegalArgumentException.class,
                () -> pagoService.calcularSubtotal(100, -1));
    }

    // ---------------------------------------------------------------------
    // calcularIVA(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("calcularIVA: calcula 19% del subtotal")
    void calcularIVA_calcula19PorCiento() {
        // Given / When
        int iva = pagoService.calcularIVA(100000);

        // Then
        assertEquals(19000, iva);
    }

    @Test
    @DisplayName("calcularIVA: subtotal negativo -> lanza IllegalArgumentException")
    void calcularIVA_subtotalNegativo_lanza() {
        // Given / When / Then
        assertThrows(IllegalArgumentException.class, () -> pagoService.calcularIVA(-1));
    }

    // ---------------------------------------------------------------------
    // historialPorAlumno(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("historialPorAlumno: devuelve los pagos del RUN")
    void historialPorAlumno_devuelvePagos() {
        // Given
        String run = "11111111-1";
        List<Pago> pagos = Arrays.asList(nuevoPago(1000, 0));
        when(pagoRepository.findByRunEstudiante(run)).thenReturn(pagos);

        // When
        List<Pago> resultado = pagoService.historialPorAlumno(run);

        // Then
        assertEquals(1, resultado.size());
        assertTrue(resultado.containsAll(pagos));
        verify(pagoRepository).findByRunEstudiante(run);
    }
}
