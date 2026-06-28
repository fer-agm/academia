package com.academia.pago_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.academia.pago_service.assemblers.PagoModelAssembler;
import com.academia.pago_service.exception.ResourceNotFoundException;
import com.academia.pago_service.model.Pago;
import com.academia.pago_service.service.PagoService;

/**
 * Pure Mockito unit tests for {@link PagoControllerV2} (HATEOAS).
 * No Spring context, no database, no MockMvc, no @WebMvcTest.
 * A request context is bound around each test because the controller builds
 * links via linkTo(methodOn(...)).
 */
@ExtendWith(MockitoExtension.class)
class PagoControllerV2Test {

    @Mock
    private PagoService pagoService;

    @Mock
    private PagoModelAssembler assembler;

    @InjectMocks
    private PagoControllerV2 pagoControllerV2;

    @BeforeEach
    void setUp() {
        // linkTo(methodOn(...)) requires bound request attributes.
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

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

    // ---------------------------------------------------------------------
    // listarPagos()
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("listarPagos: envuelve cada pago en EntityModel y agrega self-link")
    void listarPagos_ok() {
        // Given
        Pago p1 = nuevoPago(1L);
        Pago p2 = nuevoPago(2L);
        when(pagoService.listar()).thenReturn(Arrays.asList(p1, p2));
        when(assembler.toModel(p1)).thenReturn(EntityModel.of(p1));
        when(assembler.toModel(p2)).thenReturn(EntityModel.of(p2));

        // When
        CollectionModel<EntityModel<Pago>> result = pagoControllerV2.listarPagos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getLink("self").isPresent(), "Debe tener self-link");
        verify(pagoService).listar();
        verify(assembler).toModel(p1);
        verify(assembler).toModel(p2);
    }

    @Test
    @DisplayName("listarPagos: lista vacia -> CollectionModel vacio con self-link")
    void listarPagos_vacia() {
        // Given
        when(pagoService.listar()).thenReturn(List.of());

        // When
        CollectionModel<EntityModel<Pago>> result = pagoControllerV2.listarPagos();

        // Then
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertTrue(result.getLink("self").isPresent());
        verify(pagoService).listar();
    }

    // ---------------------------------------------------------------------
    // obtenerPago(...)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("obtenerPago: existe -> devuelve el EntityModel del assembler")
    void obtenerPago_existe() {
        // Given
        Pago pago = nuevoPago(1L);
        EntityModel<Pago> model = EntityModel.of(pago);
        when(pagoService.obtenerPorId(1L)).thenReturn(pago);
        when(assembler.toModel(pago)).thenReturn(model);

        // When
        EntityModel<Pago> result = pagoControllerV2.obtenerPago(1L);

        // Then
        assertNotNull(result);
        assertSame(model, result);
        assertSame(pago, result.getContent());
        verify(pagoService).obtenerPorId(1L);
        verify(assembler).toModel(pago);
    }

    @Test
    @DisplayName("obtenerPago: no existe -> propaga ResourceNotFoundException")
    void obtenerPago_noExiste() {
        // Given
        when(pagoService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Pago no existe"));

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> pagoControllerV2.obtenerPago(99L));
        verify(pagoService).obtenerPorId(99L);
    }
}
