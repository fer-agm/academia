package com.academia.pago_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academia.pago_service.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    // Para ver el historial de pagos de un alumno
    List<Pago> findByEstudianteRun(String run);
}
