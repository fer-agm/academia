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
        pago.setFecha(LocalDateTime.now());
        // Por defecto lo creamos como completado para pruebas, 
        // luego podrías integrar una pasarela real.
        pago.setEstado("COMPLETADO"); 
        return pagoRepository.save(pago);
    }

    public List<Pago> historialPorAlumno(String run) {
        return pagoRepository.findByEstudianteRun(run);
    }
}