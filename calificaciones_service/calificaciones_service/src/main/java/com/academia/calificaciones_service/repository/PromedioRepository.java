package com.academia.calificaciones_service.repository;

import com.academia.calificaciones_service.model.Promedio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromedioRepository extends JpaRepository<Promedio, Long> {
}