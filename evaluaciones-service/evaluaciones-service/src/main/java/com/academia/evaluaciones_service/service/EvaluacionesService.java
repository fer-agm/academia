package com.academia.evaluaciones_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.academia.evaluaciones_service.model.Evaluaciones;
import com.academia.evaluaciones_service.repository.EvaluacionesRepository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EvaluacionesService {

    private final EvaluacionesRepository evaluacionesRepository;

    public EvaluacionesService(EvaluacionesRepository evaluacionesRepository) {
        this.evaluacionesRepository = evaluacionesRepository;
    }

    public List<Evaluaciones> getAllEvaluaciones() {
        log.info("[EvaluacionesService] Obteniendo todas las evaluaciones");
        return evaluacionesRepository.findAll();
    }

    public Optional<Evaluaciones> getEvaluacionById(Long id) {
        log.info("[EvaluacionesService] Buscando evaluación con ID: {}", id);
        Optional<Evaluaciones> eval = evaluacionesRepository.findById(id);
        if (eval.isEmpty()) {
            log.warn("[EvaluacionesService] Evaluación con ID {} no encontrada", id);
        }
        return eval;
    }

    public Evaluaciones guardar(Evaluaciones evaluaciones) {
        log.info("[EvaluacionesService] Guardando evaluación para curso ID: {}", evaluaciones.getIdCurso());
        Evaluaciones saved = evaluacionesRepository.save(evaluaciones);
        log.info("[EvaluacionesService] Evaluación guardada con ID: {}", saved.getIdEvaluacion());
        return saved;
    }

    public void borrar(Long id) {
        log.info("[EvaluacionesService] Eliminando evaluación con ID: {}", id);
        evaluacionesRepository.deleteById(id);
        log.info("[EvaluacionesService] Evaluación con ID {} eliminada", id);
    }
}