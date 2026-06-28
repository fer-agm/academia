package com.academia.clases_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.academia.clases_service.model.Clase;
import com.academia.clases_service.repository.ClaseRepository;
import com.academia.clases_service.repository.CursoRepository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ClaseService {

    private final ClaseRepository claseRepository;
    private final CursoRepository cursoRepository;

    public ClaseService(ClaseRepository claseRepository, CursoRepository cursoRepository) {
        this.claseRepository = claseRepository;
        this.cursoRepository = cursoRepository;
    }

    public List<Clase> getAll() {
        log.info("[ClaseService] Obteniendo todas las clases");
        return claseRepository.findAll();
    }

    public Optional<Clase> getById(Long id) {
        log.info("[ClaseService] Buscando clase con ID: {}", id);
        Optional<Clase> clase = claseRepository.findById(id);
        if (clase.isEmpty()) {
            log.warn("[ClaseService] Clase con ID {} no encontrada", id);
        }
        return clase;
    }

    public List<Clase> getByCurso(Long idCurso) {
        log.info("[ClaseService] Buscando clases del curso ID: {}", idCurso);
        return claseRepository.findByIdCurso(idCurso);
    }

    public Clase guardar(Clase clase) {
        log.info("[ClaseService] Guardando clase: {}", clase.getNombreClase());
        if (clase.getIdCurso() == null || !cursoRepository.existsById(clase.getIdCurso())) {
            throw new IllegalArgumentException("El curso con id " + clase.getIdCurso() + " no existe");
        }
        Clase saved = claseRepository.save(clase);
        log.info("[ClaseService] Clase guardada con ID: {}", saved.getIdClase());
        return saved;
    }

    public void borrar(Long id) {
        log.info("[ClaseService] Eliminando clase con ID: {}", id);
        claseRepository.deleteById(id);
        log.info("[ClaseService] Clase con ID {} eliminada", id);
    }
}