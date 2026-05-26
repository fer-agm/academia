package com.academia.inscripciones_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.academia.inscripciones_service.model.Inscripciones;
import com.academia.inscripciones_service.repository.InscripcionesRepository;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class InscripcionesService {

    private final InscripcionesRepository inscripcionesRepository;

    public InscripcionesService(InscripcionesRepository inscripcionesRepository) {
        this.inscripcionesRepository = inscripcionesRepository;
    }

    public List<Inscripciones> listarTodas() {
        log.info("[InscripcionesService] Obteniendo todas las inscripciones");
        return inscripcionesRepository.findAll();
    }

    public Inscripciones buscarPorId(Long id) {
        log.info("[InscripcionesService] Buscando inscripción con ID: {}", id);
        Inscripciones ins = inscripcionesRepository.findById(id).orElse(null);
        if (ins == null) {
            log.warn("[InscripcionesService] Inscripción con ID {} no encontrada", id);
        }
        return ins;
    }

    public Inscripciones crearInscripcion(Inscripciones inscripciones) {
        log.info("[InscripcionesService] Creando inscripción para estudiante: {}", inscripciones.getIdEstudiante());
        inscripciones.setFecha_inscripcion(LocalDateTime.now());
        if (inscripciones.getEstado() == null) {
            inscripciones.setEstado("ACTIVO");
        }
        Inscripciones saved = inscripcionesRepository.save(inscripciones);
        log.info("[InscripcionesService] Inscripción creada con ID: {}", saved.getId_inscripcion());
        return saved;
    }

    public List<Inscripciones> listarPorEstudiante(String run) {
        log.info("[InscripcionesService] Buscando inscripciones del estudiante: {}", run);
        return inscripcionesRepository.findByIdEstudiante(run);
    }

    public Inscripciones actualizar(Long id, Inscripciones datos) {
        log.info("[InscripcionesService] Actualizando inscripción con ID: {}", id);
        return inscripcionesRepository.findById(id).map(ins -> {
            ins.setEstado(datos.getEstado());
            ins.setIdCurso(datos.getIdCurso());
            Inscripciones updated = inscripcionesRepository.save(ins);
            log.info("[InscripcionesService] Inscripción con ID {} actualizada", id);
            return updated;
        }).orElse(null);
    }

    public boolean eliminar(Long id) {
        log.info("[InscripcionesService] Eliminando inscripción con ID: {}", id);
        if (inscripcionesRepository.existsById(id)) {
            inscripcionesRepository.deleteById(id);
            log.info("[InscripcionesService] Inscripción con ID {} eliminada", id);
            return true;
        }
        log.warn("[InscripcionesService] Inscripción con ID {} no encontrada para eliminar", id);
        return false;
    }
}