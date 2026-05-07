package com.academia.evaluaciones_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.academia.evaluaciones_service.model.Evaluaciones;
import com.academia.evaluaciones_service.repository.EvaluacionesRepository;

@Service
public class EvaluacionesService {

    private final EvaluacionesRepository evaluacionesRepository;

    public EvaluacionesService(EvaluacionesRepository evaluacionesRepository){
        this.evaluacionesRepository = evaluacionesRepository;        
    }

    public List<Evaluaciones> getAllEvaluaciones() {
        return evaluacionesRepository.findAll();
    }

    public Optional<Evaluaciones> getEvaluacionById(Long id) {
        return evaluacionesRepository.findById(id);
    }

    public Evaluaciones guardar (Evaluaciones evaluaciones){
        return evaluacionesRepository.save(evaluaciones);
    }

    public void borrar (Long id){
        evaluacionesRepository.deleteById(id);
    }

}
