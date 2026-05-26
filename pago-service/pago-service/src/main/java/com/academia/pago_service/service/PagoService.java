package com.academia.pago_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.academia.pago_service.model.Pago;
import com.academia.pago_service.repository.PagoRepository;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public Pago registrarPago(Pago pago) {
        log.info("[PagoService] Registrando pago para estudiante: {}", pago.getRunEstudiante());
        if (pago.getFecha() == null) {
            pago.setFecha(LocalDateTime.now());
        }
        Pago saved = pagoRepository.save(pago);
        log.info("[PagoService] Pago registrado con ID: {}", saved.getId_pago());
        return saved;
    }

    public List<Pago> listarTodos() {
        log.info("[PagoService] Obteniendo todos los pagos");
        return pagoRepository.findAll();
    }

    public Pago buscarPorId(Long id) {
        log.info("[PagoService] Buscando pago con ID: {}", id);
        Pago pago = pagoRepository.findById(id).orElse(null);
        if (pago == null) {
            log.warn("[PagoService] Pago con ID {} no encontrado", id);
        }
        return pago;
    }

    public List<Pago> historialPorAlumno(String run) {
        log.info("[PagoService] Obteniendo historial de pagos para RUN: {}", run);
        return pagoRepository.findByRunEstudiante(run);
    }

    public Pago actualizarPago(Long id, Pago nuevosDatos) {
        log.info("[PagoService] Actualizando pago con ID: {}", id);
        return pagoRepository.findById(id).map(pago -> {
            pago.setMonto(nuevosDatos.getMonto());
            pago.setEstado(nuevosDatos.getEstado());
            pago.setId_curso(nuevosDatos.getId_curso());
            Pago updated = pagoRepository.save(pago);
            log.info("[PagoService] Pago con ID {} actualizado", id);
            return updated;
        }).orElse(null);
    }

    public boolean eliminarPago(Long id) {
        log.info("[PagoService] Eliminando pago con ID: {}", id);
        if (pagoRepository.existsById(id)) {
            pagoRepository.deleteById(id);
            log.info("[PagoService] Pago con ID {} eliminado", id);
            return true;
        }
        log.warn("[PagoService] Pago con ID {} no encontrado para eliminar", id);
        return false;
    }
}