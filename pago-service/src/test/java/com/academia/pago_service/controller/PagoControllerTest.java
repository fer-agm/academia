package com.academia.pago_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.academia.pago_service.dto.PagoDto;
import com.academia.pago_service.exception.ResourceNotFoundException;
import com.academia.pago_service.model.Pago;
import com.academia.pago_service.service.PagoService;

/**
 * Pure Mockito unit tests for {@link PagoController}.
 * No Spring context, no database, no MockMvc, no @WebMvcTest.
 * The controller is exercised by calling its methods directly.
 */
@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    @Mock
    private PagoService pagoService;

    @InjectMocks
    private PagoController pagoController;

    @BeforeEach
    void setUpRequestContext() {
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDownRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    /** Builds a fully-populated Pago so PagoDto.fromModel(...) has no nulls. */
    private Pago nuevoPago(Long id) {
        Pago pago = new Pago();
        pago.setId_pago(id);
        pago.setRunEstudiante("11111111-1");
        pago.setIdCurso(10L);
        pago.setValorNeto(100000);
        pago.setIva(19000);
        pago.setDescuento(0);
        pago.setTotalPagar(119000);
        pago.setMedioPago("TARJETA_CREDITO");
        pago.setFecha(LocalDateTime.of(2026, 1, 1, 12, 0));
        return pago;
    }

    /** Builds a valid PagoDto for write operations. */
    private PagoDto nuevoDto(Long id) {
        return PagoDto.fromModel(nuevoPago(id));
    }

    // ---------------------------------------------------------------------
    // crearPago(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("crearPago: delega en el servicio y devuelve 200 con el DTO")
    void crearPago_ok() {
        // Given
        PagoDto dto = nuevoDto(1L);
        Pago guardado = nuevoPago(1L);
        when(pagoService.guardar(any(Pago.class), eq("Bearer x"))).thenReturn(guardado);

        // When
        ResponseEntity<EntityModel<PagoDto>> response = pagoController.crearPago(dto, "Bearer x");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getContent());
        assertEquals(1L, response.getBody().getContent().getId_pago());
        assertEquals(119000, response.getBody().getContent().getTotalPagar());
        assertTrue(response.getBody().getLink("self").isPresent());
        verify(pagoService).guardar(any(Pago.class), eq("Bearer x"));
    }

    @Test
    @DisplayName("crearPago: authHeader null -> sigue delegando en el servicio")
    void crearPago_authHeaderNull() {
        // Given
        PagoDto dto = nuevoDto(2L);
        when(pagoService.guardar(any(Pago.class), eq((String) null))).thenReturn(nuevoPago(2L));

        // When
        ResponseEntity<EntityModel<PagoDto>> response = pagoController.crearPago(dto, null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getLink("self").isPresent());
        verify(pagoService).guardar(any(Pago.class), eq((String) null));
    }

    @Test
    @DisplayName("crearPago: el servicio lanza -> se propaga la excepcion")
    void crearPago_servicioLanza_propaga() {
        // Given
        PagoDto dto = nuevoDto(1L);
        when(pagoService.guardar(any(Pago.class), any()))
                .thenThrow(new ResourceNotFoundException("Curso no existe"));

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> pagoController.crearPago(dto, "Bearer x"));
        verify(pagoService).guardar(any(Pago.class), any());
    }

    // ---------------------------------------------------------------------
    // listarPagos()
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("listarPagos: devuelve 200 con la lista mapeada a DTO")
    void listarPagos_ok() {
        // Given
        List<Pago> pagos = Arrays.asList(nuevoPago(1L), nuevoPago(2L));
        when(pagoService.listar()).thenReturn(pagos);

        // When
        CollectionModel<EntityModel<PagoDto>> response = pagoController.listarPagos();

        // Then
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertTrue(response.getLink("self").isPresent());
        response.getContent().forEach(em -> assertTrue(em.getLink("self").isPresent()));
        verify(pagoService).listar();
    }

    @Test
    @DisplayName("listarPagos: lista vacia -> 200 con lista vacia")
    void listarPagos_vacia() {
        // Given
        when(pagoService.listar()).thenReturn(List.of());

        // When
        CollectionModel<EntityModel<PagoDto>> response = pagoController.listarPagos();

        // Then
        assertNotNull(response);
        assertEquals(0, response.getContent().size());
        assertTrue(response.getLink("self").isPresent());
        verify(pagoService).listar();
    }

    // ---------------------------------------------------------------------
    // obtenerPago(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("obtenerPago: existe -> 200 con el DTO")
    void obtenerPago_existe() {
        // Given
        when(pagoService.obtenerPorId(1L)).thenReturn(nuevoPago(1L));

        // When
        ResponseEntity<EntityModel<PagoDto>> response = pagoController.obtenerPago(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getContent());
        assertEquals(1L, response.getBody().getContent().getId_pago());
        assertTrue(response.getBody().getLink("self").isPresent());
        assertTrue(response.getBody().getLink("listar").isPresent());
        verify(pagoService).obtenerPorId(1L);
    }

    @Test
    @DisplayName("obtenerPago: no existe -> propaga ResourceNotFoundException")
    void obtenerPago_noExiste() {
        // Given
        when(pagoService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Pago no existe"));

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> pagoController.obtenerPago(99L));
        verify(pagoService).obtenerPorId(99L);
    }

    // ---------------------------------------------------------------------
    // actualizarPago(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("actualizarPago: existe -> 200 con el DTO actualizado")
    void actualizarPago_existe() {
        // Given
        PagoDto dto = nuevoDto(1L);
        Pago actualizado = nuevoPago(1L);
        when(pagoService.actualizar(eq(1L), any(Pago.class))).thenReturn(actualizado);

        // When
        ResponseEntity<EntityModel<PagoDto>> response = pagoController.actualizarPago(1L, dto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getContent());
        assertEquals(1L, response.getBody().getContent().getId_pago());
        assertTrue(response.getBody().getLink("self").isPresent());
        verify(pagoService).actualizar(eq(1L), any(Pago.class));
    }

    @Test
    @DisplayName("actualizarPago: no existe -> propaga ResourceNotFoundException")
    void actualizarPago_noExiste() {
        // Given
        PagoDto dto = nuevoDto(99L);
        when(pagoService.actualizar(eq(99L), any(Pago.class)))
                .thenThrow(new ResourceNotFoundException("Pago no existe"));

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> pagoController.actualizarPago(99L, dto));
        verify(pagoService).actualizar(eq(99L), any(Pago.class));
    }

    // ---------------------------------------------------------------------
    // eliminarPago(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("eliminarPago: existe -> 200 con mensaje de exito")
    void eliminarPago_existe() {
        // Given
        // pagoService.eliminar es void; sin stub, no hace nada

        // When
        ResponseEntity<String> response = pagoController.eliminarPago(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame("Pago Eliminado Exitosamente", response.getBody());
        verify(pagoService).eliminar(1L);
    }

    @Test
    @DisplayName("eliminarPago: no existe -> propaga ResourceNotFoundException y no devuelve mensaje")
    void eliminarPago_noExiste() {
        // Given
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Pago no existe"))
                .when(pagoService).eliminar(99L);

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> pagoController.eliminarPago(99L));
        verify(pagoService).eliminar(99L);
        verify(pagoService, never()).listar();
    }
}
