package com.academia.evaluaciones_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.academia.evaluaciones_service.model.Alternativas;
import com.academia.evaluaciones_service.repository.AlternativasRepository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AlternativasService {

    private final AlternativasRepository alternativasRepository;

    public AlternativasService(AlternativasRepository alternativasRepository) {
        this.alternativasRepository = alternativasRepository;
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