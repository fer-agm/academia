package com.academia.inscripciones_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academia.inscripciones_service.model.Inscripciones;

import java.util.List;

@Repository
public interface InscripcionesRepository extends JpaRepository<Inscripciones, Long> {
    
    // Buscar todos los cursos en los que está inscrito un alumno
    List<Inscripciones> findByIdEstudiante(String idEstudiante);
    
    // Buscar todos los alumnos inscritos en un curso específico
    List<Inscripciones> findByIdCurso(Long idCurso);
}