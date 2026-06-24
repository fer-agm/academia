package com.academia.calificaciones_service.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.academia.calificaciones_service.dto.PromedioDTO;
import com.academia.calificaciones_service.model.Promedio;
import com.academia.calificaciones_service.repository.PromedioRepository;

@Service
public class PromedioService {

    private static final Logger log = LoggerFactory.getLogger(PromedioService.class); // Log exigido 
    private final PromedioRepository repository;

    public PromedioService(PromedioRepository repository) {
        this.repository = repository;
    }

    public List<PromedioDTO> getAll() {
        log.info("Buscando todos los promedios generales");
        return repository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Optional<PromedioDTO> getById(Long id) {
        log.info("Buscando promedio con ID: {}", id);
        return repository.findById(id).map(this::convertToDTO);
    }

    public PromedioDTO guardar(PromedioDTO dto) {
        log.info("Guardando registro de promedio para estudiante: {}", dto.getIdEstudiante());
        Promedio entidad = convertToEntity(dto);
        return convertToDTO(repository.save(entidad));
    }

    public void borrar(Long id) {
        log.warn("Eliminando promedio con ID: {}", id);
        repository.deleteById(id);
    }

    private PromedioDTO convertToDTO(Promedio entity) {
        return new PromedioDTO(entity.getIdPromedio(), entity.getIdCurso(), entity.getIdEstudiante(), entity.getPromedioGeneral(), entity.getTotalEvaluaciones());
    }

    private Promedio convertToEntity(PromedioDTO dto) {
        return new Promedio(dto.getIdPromedio(), dto.getIdEstudiante(), dto.getIdCurso(), dto.getPromedioGeneral(), dto.getTotalEvaluaciones());
    }
}