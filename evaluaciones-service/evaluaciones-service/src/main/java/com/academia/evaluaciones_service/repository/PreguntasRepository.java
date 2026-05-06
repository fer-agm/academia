
package com.academia.evaluaciones_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academia.evaluaciones_service.model.Preguntas;

@Repository 
public interface PreguntasRepository extends JpaRepository<Preguntas,Long>{

}