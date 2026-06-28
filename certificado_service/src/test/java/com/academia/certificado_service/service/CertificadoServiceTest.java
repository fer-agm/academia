package com.academia.certificado_service.service;

import com.academia.certificado_service.model.Certificado;
import com.academia.certificado_service.repository.CertificadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pure Mockito unit tests for {@link CertificadoService}.
 * No Spring context, no database, no @SpringBootTest.
 */
@ExtendWith(MockitoExtension.class)
class CertificadoServiceTest {

    @Mock
    private CertificadoRepository repository;

    @InjectMocks
    private CertificadoService service;

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

    // ---------- listarTodos ----------

    @Test
    void listarTodos_devuelveListaDelRepositorio() {
        // Given
        Certificado c1 = buildCertificado(1L, "E1", 10L, LocalDateTime.now(), "AAA111");
        Certificado c2 = buildCertificado(2L, "E2", 20L, LocalDateTime.now(), "BBB222");
        when(repository.findAll()).thenReturn(List.of(c1, c2));

        // When
        List<Certificado> result = service.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(c1, result.get(0));
        assertEquals(c2, result.get(1));
        verify(repository, times(1)).findAll();
    }

    @Test
    void listarTodos_listaVacia() {
        // Given
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Certificado> result = service.listarTodos();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    // ---------- getById ----------

    @Test
    void getById_encontrado() {
        // Given
        Certificado c = buildCertificado(5L, "E5", 50L, LocalDateTime.now(), "CCC333");
        when(repository.findById(5L)).thenReturn(Optional.of(c));

        // When
        Optional<Certificado> result = service.getById(5L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(c, result.get());
        verify(repository, times(1)).findById(5L);
    }

    @Test
    void getById_noEncontrado() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Certificado> result = service.getById(99L);

        // Then
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findById(99L);
    }

    // ---------- listarPorEstudiante ----------

    @Test
    void listarPorEstudiante_conResultados() {
        // Given
        Certificado c1 = buildCertificado(1L, "EST-1", 10L, LocalDateTime.now(), "AAA111");
        Certificado c2 = buildCertificado(2L, "EST-1", 11L, LocalDateTime.now(), "AAA222");
        when(repository.findByIdEstudiante("EST-1")).thenReturn(List.of(c1, c2));

        // When
        List<Certificado> result = service.listarPorEstudiante("EST-1");

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> "EST-1".equals(c.getIdEstudiante())));
        verify(repository, times(1)).findByIdEstudiante("EST-1");
    }

    @Test
    void listarPorEstudiante_sinResultados() {
        // Given
        when(repository.findByIdEstudiante("DESCONOCIDO")).thenReturn(Collections.emptyList());

        // When
        List<Certificado> result = service.listarPorEstudiante("DESCONOCIDO");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findByIdEstudiante("DESCONOCIDO");
    }

    // ---------- generarCertificado (nuevo / id null) ----------

    @Test
    void generarCertificado_nuevo_asignaFechaYCodigo() {
        // Given
        Certificado nuevo = buildCertificado(null, "E1", 10L, null, null);
        when(repository.save(any(Certificado.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Certificado result = service.generarCertificado(nuevo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getFechaEmision(), "Debe asignar fechaEmision al ser nuevo");
        assertNotNull(result.getCodigo(), "Debe generar un codigo al ser nuevo");
        assertEquals(8, result.getCodigo().length(), "El codigo se trunca a 8 caracteres");
        assertEquals(result.getCodigo().toUpperCase(), result.getCodigo(), "El codigo debe estar en mayusculas");
        // No debe consultar findById cuando el id es null
        verify(repository, never()).findById(anyLong());
        verify(repository, times(1)).save(nuevo);
    }

    @Test
    void generarCertificado_nuevo_ignoraValoresEntrantesDeFechaYCodigo() {
        // Given: aunque vengan datos, al ser nuevo (id null) se sobrescriben
        LocalDateTime fechaEntrante = LocalDateTime.of(2000, 1, 1, 0, 0);
        Certificado nuevo = buildCertificado(null, "E1", 10L, fechaEntrante, "VIEJO");
        when(repository.save(any(Certificado.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Certificado result = service.generarCertificado(nuevo);

        // Then
        assertFalse(fechaEntrante.equals(result.getFechaEmision()) && "VIEJO".equals(result.getCodigo()),
                "La fecha y el codigo entrantes deben ser reemplazados");
        assertEquals(8, result.getCodigo().length());
        verify(repository, never()).findById(anyLong());
        verify(repository, times(1)).save(nuevo);
    }

    // ---------- generarCertificado (actualizacion / id presente) ----------

    @Test
    void generarCertificado_existente_conservaFechaYCodigoCuandoVienenNulos() {
        // Given
        LocalDateTime fechaExistente = LocalDateTime.of(2025, 5, 1, 12, 0);
        Certificado existente = buildCertificado(7L, "E7", 70L, fechaExistente, "CODE0007");
        Certificado entrante = buildCertificado(7L, "E7", 70L, null, null);
        when(repository.findById(7L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Certificado.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Certificado result = service.generarCertificado(entrante);

        // Then
        assertEquals(fechaExistente, result.getFechaEmision(), "Debe conservar la fecha existente");
        assertEquals("CODE0007", result.getCodigo(), "Debe conservar el codigo existente");
        verify(repository, times(1)).findById(7L);
        verify(repository, times(1)).save(entrante);
    }

    @Test
    void generarCertificado_existente_respetaFechaYCodigoEntrantes() {
        // Given
        LocalDateTime fechaExistente = LocalDateTime.of(2025, 5, 1, 12, 0);
        LocalDateTime fechaNueva = LocalDateTime.of(2026, 6, 27, 9, 30);
        Certificado existente = buildCertificado(8L, "E8", 80L, fechaExistente, "OLDCODE8");
        Certificado entrante = buildCertificado(8L, "E8", 80L, fechaNueva, "NEWCODE8");
        when(repository.findById(8L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Certificado.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Certificado result = service.generarCertificado(entrante);

        // Then
        assertEquals(fechaNueva, result.getFechaEmision(), "No debe sobrescribir la fecha entrante");
        assertEquals("NEWCODE8", result.getCodigo(), "No debe sobrescribir el codigo entrante");
        verify(repository, times(1)).findById(8L);
        verify(repository, times(1)).save(entrante);
    }

    @Test
    void generarCertificado_existente_noEncontrado_guardaTalCual() {
        // Given: id presente pero no existe en BD -> ifPresent no ejecuta nada
        Certificado entrante = buildCertificado(15L, "E15", 150L, null, null);
        when(repository.findById(15L)).thenReturn(Optional.empty());
        when(repository.save(any(Certificado.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Certificado result = service.generarCertificado(entrante);

        // Then
        assertSame(entrante, result);
        // Como no existia, fecha y codigo permanecen nulos (no se autogeneran en rama de update)
        assertEquals(null, result.getFechaEmision());
        assertEquals(null, result.getCodigo());
        verify(repository, times(1)).findById(15L);
        verify(repository, times(1)).save(entrante);
    }

    @Test
    void generarCertificado_devuelveEntidadGuardada() {
        // Given
        Certificado entrante = buildCertificado(null, "E1", 10L, null, null);
        Certificado guardado = buildCertificado(100L, "E1", 10L, LocalDateTime.now(), "SAVED001");
        when(repository.save(any(Certificado.class))).thenReturn(guardado);

        // When
        Certificado result = service.generarCertificado(entrante);

        // Then
        assertSame(guardado, result, "Debe devolver exactamente lo que retorna repository.save");
        verify(repository, times(1)).save(entrante);
    }

    // ---------- borrar ----------

    @Test
    void borrar_invocaDeleteById() {
        // Given
        Long id = 42L;

        // When
        service.borrar(id);

        // Then
        verify(repository, times(1)).deleteById(id);
        verify(repository, never()).findById(anyLong());
    }

    @Test
    void borrar_noInteractuaConOtrosMetodos() {
        // Given
        Long id = 1L;

        // When
        service.borrar(id);

        // Then
        verify(repository, times(1)).deleteById(id);
        verify(repository, never()).findAll();
        verify(repository, never()).save(any());
    }
}
