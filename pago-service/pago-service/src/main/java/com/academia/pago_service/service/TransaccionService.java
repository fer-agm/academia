package com.academia.pago_service.service;

import com.academia.pago_service.model.Transaccion;
import com.academia.pago_service.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransaccionService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    public List<Transaccion> listarTodas() {
        return transaccionRepository.findAll();
    }

    public Transaccion buscarPorId(Long id) {
        return transaccionRepository.findById(id).orElse(null);
    }

    public Transaccion registrarTransaccion(Transaccion t) {
        if (t.getFecha() == null) {
            t.setFecha(LocalDateTime.now());
        }
        return transaccionRepository.save(t);
    }

    public Transaccion actualizarTransaccion(Long id, Transaccion nuevosDatos) {
        return transaccionRepository.findById(id).map(t -> {
            t.setMetodo(nuevosDatos.getMetodo());
            return transaccionRepository.save(t);
        }).orElse(null);
    }

    public boolean eliminar(Long id) {
        if (transaccionRepository.existsById(id)) {
            transaccionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}