package com.academia.clases_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academia.clases_service.model.Clase;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {
    List<Clase> findByIdCurso(Long idCurso);
}