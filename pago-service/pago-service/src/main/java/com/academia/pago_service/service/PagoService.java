package com.academia.pago_service.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.academia.pago_service.model.Pago;
import com.academia.pago_service.repository.PagoRepository;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

   
    public Pago registrarPago(Pago pago) {
        if (pago.getFecha() == null) {
            pago.setFecha(LocalDateTime.now()); // Usamos LocalDateTime según tu modelo
        }
        return pagoRepository.save(pago);
    }

  
    public List<Pago> listarTodos() {
        return pagoRepository.findAll();
    }

    public Pago buscarPorId(Long id) {
        return pagoRepository.findById(id).orElse(null);
    }

    public List<Pago> historialPorAlumno(String run) {
        return pagoRepository.findByRunEstudiante(run);
    }

    public Pago actualizarPago(Long id, Pago nuevosDatos) {
        return pagoRepository.findById(id).map(pago -> {
            pago.setMonto(nuevosDatos.getMonto());
            pago.setEstado(nuevosDatos.getEstado());
            pago.setId_curso(nuevosDatos.getId_curso());
            // Normalmente no editamos el RUN ni la fecha original
            return pagoRepository.save(pago);
        }).orElse(null);
    }

    public boolean eliminarPago(Long id) {
        if (pagoRepository.existsById(id)) {
            pagoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}