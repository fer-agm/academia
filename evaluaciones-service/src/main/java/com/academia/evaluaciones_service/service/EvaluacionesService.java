package com.academia.evaluaciones_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.academia.evaluaciones_service.model.Evaluaciones;
import com.academia.evaluaciones_service.repository.EvaluacionesRepository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EvaluacionesService {

    private final EvaluacionesRepository evaluacionesRepository;
    private final WebClient webClient;

    @Value("${api.curso.exists}")
    private String cursoExistsUrl;

    public EvaluacionesService(EvaluacionesRepository evaluacionesRepository, WebClient webClient) {
        this.evaluacionesRepository = evaluacionesRepository;
        this.webClient = webClient;
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

    public boolean existe(Long id) {
        return evaluacionesRepository.existsById(id);
    }

    public Evaluaciones guardar(Evaluaciones eval, String authHeader) {
        log.info("[EvaluacionesService] Guardando evaluación para curso ID: {}", eval.getIdCurso());

        Boolean existe = webClient.get()
                .uri(String.format(cursoExistsUrl, eval.getIdCurso()))
                .headers(h -> { if (authHeader != null) h.set("Authorization", authHeader); })
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (existe == null) {
            throw new IllegalStateException("No se pudo validar el curso con id " + eval.getIdCurso());
        }
        if (Boolean.FALSE.equals(existe)) {
            throw new IllegalArgumentException("El curso con id " + eval.getIdCurso() + " no existe");
        }

        Evaluaciones saved = evaluacionesRepository.save(eval);
        log.info("[EvaluacionesService] Evaluación guardada con ID: {}", saved.getIdEvaluacion());
        return saved;
    }

    public void borrar(Long id) {
        log.info("[EvaluacionesService] Eliminando evaluación con ID: {}", id);
        evaluacionesRepository.deleteById(id);
        log.info("[EvaluacionesService] Evaluación con ID {} eliminada", id);
    }
}
