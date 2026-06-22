package com.academia.calificaciones_service.service;

import com.academia.calificaciones_service.dto.CalificacionDTO;
import com.academia.calificaciones_service.model.Calificacion;
import com.academia.calificaciones_service.repository.CalificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CalificacionService {

    private static final Logger log = LoggerFactory.getLogger(CalificacionService.class); // Log exigido 
    private final CalificacionRepository repository;

    public CalificacionService(CalificacionRepository repository) {
        this.repository = repository;
    }

    public List<CalificacionDTO> getAll() {
        log.info("Buscando todas las calificaciones");
        return repository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Optional<CalificacionDTO> getById(Long id) {
        log.info("Buscando calificación con ID: {}", id);
        return repository.findById(id).map(this::convertToDTO);
    }

    public CalificacionDTO guardar(CalificacionDTO dto) {
        log.info("Guardando calificación para estudiante: {}", dto.getIdEstudiante());
        Calificacion entidad = convertToEntity(dto);
        return convertToDTO(repository.save(entidad));
    }

    public void borrar(Long id) {
        log.warn("Eliminando calificación con ID: {}", id);
        repository.deleteById(id);
    }

    private CalificacionDTO convertToDTO(Calificacion entity) {
        return new CalificacionDTO(entity.getIdEvaluacion(), entity.getIdEstudiante(), entity.getFecha(), entity.getNota());
    }

    private Calificacion convertToEntity(CalificacionDTO dto) {
        return new Calificacion(dto.getIdEvaluacion(), dto.getIdEstudiante(), dto.getFecha(), dto.getNota());
    }
}