package com.academia.evaluaciones_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academia.evaluaciones_service.model.Alternativas;

@Repository 
public interface AlternativasRepository extends JpaRepository<Alternativas,Long>{

}
