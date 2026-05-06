package com.academia.evaluaciones_service.service;

import org.springframework.stereotype.Service;

import com.academia.evaluaciones_service.model.Evaluaciones;
import com.academia.evaluaciones_service.repository.EvaluacionesRepository;

@Service
public class EvaluacionesService {

    private final EvaluacionesRepository evaluacionesRepository;

    public EvaluacionesService(EvaluacionesRepository evaluacionesRepository){
        this.evaluacionesRepository = evaluacionesRepository;        
    }

    public Evaluaciones guardar (Evaluaciones evaluaciones){
        return evaluacionesRepository.save(evaluaciones);
    }

    public void borrar (Evaluaciones evaluaciones){
        evaluacionesRepository.delete(evaluaciones);
    }

}
