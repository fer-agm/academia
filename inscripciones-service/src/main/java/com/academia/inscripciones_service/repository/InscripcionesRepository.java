package com.academia.inscripciones_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academia.inscripciones_service.model.Inscripciones;

import java.util.List;

@Repository
public interface InscripcionesRepository extends JpaRepository<Inscripciones, Long> {
    
    List<Inscripciones> findByIdEstudiante(String idEstudiante);
    
    List<Inscripciones> findByIdCurso(Long idCurso);
}