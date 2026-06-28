package com.academia.certificado_service.controller;

import com.academia.certificado_service.model.Certificado;
import com.academia.certificado_service.service.CertificadoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pure Mockito unit tests for {@link CertificadoController}.
 * No Spring context, no database, no @SpringBootTest, no @WebMvcTest, no MockMvc.
 * Controller methods are invoked directly and the {@link ResponseEntity} is asserted.
 */
@ExtendWith(MockitoExtension.class)
class CertificadoControllerTest {

    @Mock
    private CertificadoService certificadoService;

    @InjectMocks
    private CertificadoController controller;

    // ---------- helpers ----------

    private Certificado buildCertificado(Long id, String idEstudiante, Long idCurso,
                                         LocalDateTime fechaEmision, String codigo) {
        Certificado c = new Certificado();
        c.setIdCertificado(id);
        c.setIdEstudiante(idEstudiante);
        c.setIdCurso(idCurso);
        c.setFechaEmision(fechaEmision);
        c.setCodigo(codigo);
        return c;
    }

    // ---------- getAll (GET /listar) ----------

    @Test
    void getAll_devuelveOkConLista() {
        // Given
        Certificado c1 = buildCertificado(1L, "E1", 10L, LocalDateTime.now(), "AAA111");
        Certificado c2 = buildCertificado(2L, "E2", 20L, LocalDateTime.now(), "BBB222");
        when(certificadoService.listarTodos()).thenReturn(List.of(c1, c2));

        // When
        ResponseEntity<List<Certificado>> response = controller.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertSame(c1, response.getBody().get(0));
        verify(certificadoService, times(1)).listarTodos();
    }

    @Test
    void getAll_devuelveOkConListaVacia() {
        // Given
        when(certificadoService.listarTodos()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<Certificado>> response = controller.getAll();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(certificadoService, times(1)).listarTodos();
    }

    // ---------- getById (GET /{id}) ----------

    @Test
    void getById_encontrado_devuelveOk() {
        // Given
        Certificado c = buildCertificado(5L, "E5", 50L, LocalDateTime.now(), "CCC333");
        when(certificadoService.getById(5L)).thenReturn(Optional.of(c));

        // When
        ResponseEntity<Certificado> response = controller.getById(5L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(c, response.getBody());
        verify(certificadoService, times(1)).getById(5L);
    }

    @Test
    void getById_noEncontrado_devuelveNotFound() {
        // Given
        when(certificadoService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Certificado> response = controller.getById(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(certificadoService, times(1)).getById(99L);
    }

    // ---------- buscarPorEstudiante (GET /estudiante/{idEstudiante}) ----------

    @Test
    void buscarPorEstudiante_conResultados_devuelveOk() {
        // Given
        Certificado c1 = buildCertificado(1L, "EST-1", 10L, LocalDateTime.now(), "AAA111");
        Certificado c2 = buildCertificado(2L, "EST-1", 11L, LocalDateTime.now(), "AAA222");
        when(certificadoService.listarPorEstudiante("EST-1")).thenReturn(List.of(c1, c2));

        // When
        ResponseEntity<List<Certificado>> response = controller.buscarPorEstudiante("EST-1");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(certificadoService, times(1)).listarPorEstudiante("EST-1");
    }

    @Test
    void buscarPorEstudiante_sinResultados_devuelveOkVacio() {
        // Given
        when(certificadoService.listarPorEstudiante("DESCONOCIDO")).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<Certificado>> response = controller.buscarPorEstudiante("DESCONOCIDO");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(certificadoService, times(1)).listarPorEstudiante("DESCONOCIDO");
    }

    // ---------- crear (POST /generar) ----------

    @Test
    void crear_devuelveOkConEntidadGenerada() {
        // Given
        Certificado entrante = buildCertificado(null, "E1", 10L, null, null);
        Certificado generado = buildCertificado(100L, "E1", 10L, LocalDateTime.now(), "SAVED001");
        when(certificadoService.generarCertificado(entrante)).thenReturn(generado);

        // When
        ResponseEntity<Certificado> response = controller.crear(entrante);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(generado, response.getBody());
        verify(certificadoService, times(1)).generarCertificado(entrante);
    }

    // ---------- actualizar (PUT /{id}) ----------

    @Test
    void actualizar_encontrado_devuelveOkYSeteaIdDeLaUrl() {
        // Given
        Certificado existente = buildCertificado(7L, "E7", 70L, LocalDateTime.now(), "CODE0007");
        Certificado entrante = buildCertificado(null, "E7-mod", 71L, null, null);
        Certificado guardado = buildCertificado(7L, "E7-mod", 71L, LocalDateTime.now(), "CODE0007");
        when(certificadoService.getById(7L)).thenReturn(Optional.of(existente));
        when(certificadoService.generarCertificado(any(Certificado.class))).thenReturn(guardado);

        // When
        ResponseEntity<Certificado> response = controller.actualizar(7L, entrante);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(guardado, response.getBody());
        // El id de la URL debe haberse seteado en el cuerpo entrante antes de guardar
        assertEquals(7L, entrante.getIdCertificado());
        verify(certificadoService, times(1)).getById(7L);
        verify(certificadoService, times(1)).generarCertificado(entrante);
    }

    @Test
    void actualizar_noEncontrado_devuelveNotFoundYNoGuarda() {
        // Given
        Certificado entrante = buildCertificado(null, "E15", 150L, null, null);
        when(certificadoService.getById(15L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Certificado> response = controller.actualizar(15L, entrante);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(certificadoService, times(1)).getById(15L);
        verify(certificadoService, never()).generarCertificado(any(Certificado.class));
    }

    // ---------- borrar (DELETE /{id}) ----------

    @Test
    void borrar_encontrado_devuelveNoContentYBorra() {
        // Given
        Certificado existente = buildCertificado(42L, "E42", 420L, LocalDateTime.now(), "DEL00042");
        when(certificadoService.getById(42L)).thenReturn(Optional.of(existente));

        // When
        ResponseEntity<Void> response = controller.borrar(42L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(certificadoService, times(1)).getById(42L);
        verify(certificadoService, times(1)).borrar(42L);
    }

    @Test
    void borrar_noEncontrado_devuelveNotFoundYNoBorra() {
        // Given
        when(certificadoService.getById(99L)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Void> response = controller.borrar(99L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(certificadoService, times(1)).getById(99L);
        verify(certificadoService, never()).borrar(99L);
    }
}
