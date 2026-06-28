package com.academia.pago_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.academia.pago_service.exception.BadRequestException;
import com.academia.pago_service.model.Pago;
import com.academia.pago_service.model.Transaccion;
import com.academia.pago_service.repository.PagoRepository;
import com.academia.pago_service.repository.TransaccionRepository;

/**
 * Pure Mockito unit tests for {@link TransaccionService}.
 * No Spring context, no database, no @SpringBootTest.
 */
@ExtendWith(MockitoExtension.class)
class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private TransaccionService transaccionService;

    private Transaccion nuevaTransaccion(Long id, String metodo) {
        Transaccion t = new Transaccion();
        t.setId_transaccion(id);
        t.setMetodo(metodo);
        // Cada transacción referencia un Pago existente (id_pago=1)
        Pago pago = new Pago();
        pago.setId_pago(1L);
        t.setPago(pago);
        return t;
    }

    // ---------------------------------------------------------------------
    // listarTodas()
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("listarTodas: devuelve la lista del repositorio")
    void listarTodas_devuelveLista() {
        // Given
        List<Transaccion> transacciones = Arrays.asList(
                nuevaTransaccion(1L, "TARJETA_CREDITO"),
                nuevaTransaccion(2L, "DEBITO"));
        when(transaccionRepository.findAll()).thenReturn(transacciones);

        // When
        List<Transaccion> resultado = transaccionService.listarTodas();

        // Then
        assertEquals(2, resultado.size());
        verify(transaccionRepository).findAll();
    }

    // ---------------------------------------------------------------------
    // buscarPorId(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("buscarPorId: existe -> devuelve la transacción")
    void buscarPorId_existe_devuelve() {
        // Given
        Transaccion t = nuevaTransaccion(1L, "PAYPAL");
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(t));

        // When
        Transaccion resultado = transaccionService.buscarPorId(1L);

        // Then
        assertSame(t, resultado);
        verify(transaccionRepository).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId: no existe -> devuelve null")
    void buscarPorId_noExiste_devuelveNull() {
        // Given
        when(transaccionRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Transaccion resultado = transaccionService.buscarPorId(99L);

        // Then
        assertNull(resultado);
        verify(transaccionRepository).findById(99L);
    }

    // ---------------------------------------------------------------------
    // registrarTransaccion(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("registrarTransaccion: sin fecha -> asigna fecha actual y guarda")
    void registrarTransaccion_sinFecha_asignaFecha() {
        // Given
        Transaccion t = nuevaTransaccion(null, "TARJETA_CREDITO");
        when(pagoRepository.existsById(anyLong())).thenReturn(true);
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Transaccion resultado = transaccionService.registrarTransaccion(t);

        // Then
        assertNotNull(resultado.getFecha(), "Debe asignar fecha cuando es null");
        verify(transaccionRepository, times(1)).save(t);
    }

    @Test
    @DisplayName("registrarTransaccion: con fecha -> respeta la fecha existente")
    void registrarTransaccion_conFecha_respetaFecha() {
        // Given
        LocalDateTime fecha = LocalDateTime.of(2021, 5, 5, 10, 30);
        Transaccion t = nuevaTransaccion(null, "DEBITO");
        t.setFecha(fecha);
        when(pagoRepository.existsById(anyLong())).thenReturn(true);
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Transaccion resultado = transaccionService.registrarTransaccion(t);

        // Then
        assertEquals(fecha, resultado.getFecha());
        verify(transaccionRepository).save(t);
    }

    @Test
    @DisplayName("registrarTransaccion: pago referenciado no existe -> lanza BadRequestException y no guarda")
    void registrarTransaccion_pagoNoExiste_lanza() {
        // Given
        Transaccion t = nuevaTransaccion(null, "TARJETA_CREDITO");
        when(pagoRepository.existsById(anyLong())).thenReturn(false);

        // When / Then
        assertThrows(BadRequestException.class,
                () -> transaccionService.registrarTransaccion(t));
        verify(transaccionRepository, never()).save(any(Transaccion.class));
    }

    @Test
    @DisplayName("registrarTransaccion: pago nulo -> lanza BadRequestException y no guarda")
    void registrarTransaccion_pagoNulo_lanza() {
        // Given
        Transaccion t = nuevaTransaccion(null, "TARJETA_CREDITO");
        t.setPago(null);

        // When / Then
        assertThrows(BadRequestException.class,
                () -> transaccionService.registrarTransaccion(t));
        verify(transaccionRepository, never()).save(any(Transaccion.class));
    }

    // ---------------------------------------------------------------------
    // actualizarTransaccion(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("actualizarTransaccion: existe -> actualiza método y guarda")
    void actualizarTransaccion_existe_actualiza() {
        // Given
        Transaccion existente = nuevaTransaccion(1L, "DEBITO");
        Transaccion nuevosDatos = nuevaTransaccion(null, "PAYPAL");
        when(transaccionRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(transaccionRepository.save(any(Transaccion.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Transaccion resultado = transaccionService.actualizarTransaccion(1L, nuevosDatos);

        // Then
        assertNotNull(resultado);
        assertEquals("PAYPAL", resultado.getMetodo());
        verify(transaccionRepository).save(existente);
    }

    @Test
    @DisplayName("actualizarTransaccion: no existe -> devuelve null y no guarda")
    void actualizarTransaccion_noExiste_devuelveNull() {
        // Given
        Transaccion nuevosDatos = nuevaTransaccion(null, "PAYPAL");
        when(transaccionRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Transaccion resultado = transaccionService.actualizarTransaccion(99L, nuevosDatos);

        // Then
        assertNull(resultado);
        verify(transaccionRepository, never()).save(any(Transaccion.class));
    }

    // ---------------------------------------------------------------------
    // eliminar(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("eliminar: existe -> borra y devuelve true")
    void eliminar_existe_devuelveTrue() {
        // Given
        when(transaccionRepository.existsById(1L)).thenReturn(true);

        // When
        boolean resultado = transaccionService.eliminar(1L);

        // Then
        assertTrue(resultado);
        verify(transaccionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar: no existe -> devuelve false y no borra")
    void eliminar_noExiste_devuelveFalse() {
        // Given
        when(transaccionRepository.existsById(99L)).thenReturn(false);

        // When
        boolean resultado = transaccionService.eliminar(99L);

        // Then
        assertFalse(resultado);
        verify(transaccionRepository, never()).deleteById(any());
    }
}
