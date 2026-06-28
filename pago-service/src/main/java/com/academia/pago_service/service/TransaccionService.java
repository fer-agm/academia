package com.academia.pago_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.academia.pago_service.model.Transaccion;
import com.academia.pago_service.repository.TransaccionRepository;
import com.academia.pago_service.repository.PagoRepository;
import com.academia.pago_service.exception.BadRequestException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final PagoRepository pagoRepository;

    public TransaccionService(TransaccionRepository transaccionRepository, PagoRepository pagoRepository) {
        this.transaccionRepository = transaccionRepository;
        this.pagoRepository = pagoRepository;
    }

    public List<Transaccion> listarTodas() {
        log.info("[TransaccionService] Obteniendo todas las transacciones");
        return transaccionRepository.findAll();
    }

    public Transaccion buscarPorId(Long id) {
        log.info("[TransaccionService] Buscando transacción con ID: {}", id);
        Transaccion t = transaccionRepository.findById(id).orElse(null);
        if (t == null) {
            log.warn("[TransaccionService] Transacción con ID {} no encontrada", id);
        }
        return t;
    }

    public Transaccion registrarTransaccion(Transaccion t) {
        log.info("[TransaccionService] Registrando transacción con método: {}", t.getMetodo());
        if (t.getPago() == null || t.getPago().getId_pago() == null
                || !pagoRepository.existsById(t.getPago().getId_pago())) {
            throw new BadRequestException("El pago con id "
                    + (t.getPago() == null ? null : t.getPago().getId_pago()) + " no existe");
        }
        if (t.getFecha() == null) {
            t.setFecha(LocalDateTime.now());
        }
        Transaccion saved = transaccionRepository.save(t);
        log.info("[TransaccionService] Transacción registrada con ID: {}", saved.getId_transaccion());
        return saved;
    }

    public Transaccion actualizarTransaccion(Long id, Transaccion nuevosDatos) {
        log.info("[TransaccionService] Actualizando transacción con ID: {}", id);
        return transaccionRepository.findById(id).map(t -> {
            t.setMetodo(nuevosDatos.getMetodo());
            Transaccion updated = transaccionRepository.save(t);
            log.info("[TransaccionService] Transacción con ID {} actualizada", id);
            return updated;
        }).orElse(null);
    }

    public boolean eliminar(Long id) {
        log.info("[TransaccionService] Eliminando transacción con ID: {}", id);
        if (transaccionRepository.existsById(id)) {
            transaccionRepository.deleteById(id);
            log.info("[TransaccionService] Transacción con ID {} eliminada", id);
            return true;
        }
        log.warn("[TransaccionService] Transacción con ID {} no encontrada para eliminar", id);
        return false;
    }
}