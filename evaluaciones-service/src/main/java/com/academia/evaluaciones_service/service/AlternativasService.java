package com.academia.evaluaciones_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.academia.evaluaciones_service.model.Alternativas;
import com.academia.evaluaciones_service.repository.AlternativasRepository;
import com.academia.evaluaciones_service.repository.PreguntasRepository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AlternativasService {

    private final AlternativasRepository alternativasRepository;
    private final PreguntasRepository preguntasRepository;

    public AlternativasService(AlternativasRepository alternativasRepository,
            PreguntasRepository preguntasRepository) {
        this.alternativasRepository = alternativasRepository;
        this.preguntasRepository = preguntasRepository;
    }

    public List<Alternativas> getAll() {
        log.info("[AlternativasService] Obteniendo todas las alternativas");
        return alternativasRepository.findAll();
    }

    public Optional<Alternativas> getById(Long id) {
        log.info("[AlternativasService] Buscando alternativa con ID: {}", id);
        Optional<Alternativas> alt = alternativasRepository.findById(id);
        if (alt.isEmpty()) {
            log.warn("[AlternativasService] Alternativa con ID {} no encontrada", id);
        }
        return alt;
    }

    public Alternativas guardar(Alternativas alternativas) {
        log.info("[AlternativasService] Guardando alternativa: {}", alternativas.getTexto());
        if (alternativas.getIdPregunta() == null
                || !preguntasRepository.existsById(alternativas.getIdPregunta())) {
            throw new IllegalArgumentException(
                    "La pregunta con id " + alternativas.getIdPregunta() + " no existe");
        }
        Alternativas saved = alternativasRepository.save(alternativas);
        log.info("[AlternativasService] Alternativa guardada con ID: {}", saved.getIdAlternativa());
        return saved;
    }

    public void borrar(Long id) {
        log.info("[AlternativasService] Eliminando alternativa con ID: {}", id);
        alternativasRepository.deleteById(id);
        log.info("[AlternativasService] Alternativa con ID {} eliminada", id);
    }
}