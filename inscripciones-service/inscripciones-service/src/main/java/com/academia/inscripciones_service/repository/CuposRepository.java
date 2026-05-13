package com.academia.inscripciones_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academia.inscripciones_service.model.Cupos;

import java.util.Optional;

@Repository
public interface CuposRepository extends JpaRepository<Cupos, Long> {
    // Buscar cupos por el ID del curso
    Optional<Cupos> findByIdCurso(Long idCurso);
}