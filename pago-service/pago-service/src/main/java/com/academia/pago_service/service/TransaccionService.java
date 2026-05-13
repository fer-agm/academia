package com.academia.pago_service.service;

import com.academia.pago_service.model.Transaccion;
import com.academia.pago_service.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class TransaccionService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    public Transaccion registrarTransaccion(Transaccion t) {
        t.setFecha(LocalDateTime.now());
        return transaccionRepository.save(t);
    }
}