package com.academia.calificaciones_service.service;

import com.academia.calificaciones_service.dto.CalificacionDTO;
import com.academia.calificaciones_service.model.Calificacion;
import com.academia.calificaciones_service.repository.CalificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Optional;

@Service
public class CalificacionService {

    private static final Logger log = LoggerFactory.getLogger(CalificacionService.class);
    private final CalificacionRepository repository;
    private final WebClient webClient;

    @Value("${api.evaluacion.exists}")
    private String evaluacionExistsUrl;

    @Value("${api.usuario.exists}")
    private String usuarioExistsUrl;

    public CalificacionService(CalificacionRepository repository, WebClient webClient) {
        this.repository = repository;
        this.webClient = webClient;
    }

    public List<CalificacionDTO> getAll() {
        log.info("Buscando todas las calificaciones");
        return repository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Optional<CalificacionDTO> getById(Long id) {
        log.info("Buscando calificación con ID: {}", id);
        return repository.findById(id).map(this::convertToDTO);
    }

    public CalificacionDTO guardar(CalificacionDTO dto, String authHeader) {
        log.info("Guardando calificación de la evaluación {} para el estudiante {}", dto.getIdEvaluacion(), dto.getIdEstudiante());
        // Las referencias deben existir (validación cross-service, reenviando el token):
        verificarExiste(evaluacionExistsUrl, dto.getIdEvaluacion(), authHeader,
                "La evaluación con id " + dto.getIdEvaluacion() + " no existe");
        verificarExiste(usuarioExistsUrl, dto.getIdEstudiante(), authHeader,
                "El estudiante con RUN " + dto.getIdEstudiante() + " no existe");
        Calificacion entidad = convertToEntity(dto);
        return convertToDTO(repository.save(entidad));
    }

    private void verificarExiste(String url, Object id, String authHeader, String mensajeNoExiste) {
        Boolean existe = webClient.get().uri(String.format(url, id))
                .headers(h -> { if (authHeader != null) h.set("Authorization", authHeader); })
                .retrieve().bodyToMono(Boolean.class).block();
        if (existe == null) throw new IllegalStateException("No se pudo validar: " + mensajeNoExiste);
        if (Boolean.FALSE.equals(existe)) throw new IllegalArgumentException(mensajeNoExiste);
    }

    public void borrar(Long id) {
        log.warn("Eliminando calificación con ID: {}", id);
        repository.deleteById(id);
    }

    private CalificacionDTO convertToDTO(Calificacion e) {
        return new CalificacionDTO(e.getIdCalificacion(), e.getIdEvaluacion(), e.getIdEstudiante(), e.getFecha(), e.getNota());
    }

    private Calificacion convertToEntity(CalificacionDTO dto) {
        return new Calificacion(dto.getIdCalificacion(), dto.getIdEvaluacion(), dto.getIdEstudiante(), dto.getFecha(), dto.getNota());
    }
}
